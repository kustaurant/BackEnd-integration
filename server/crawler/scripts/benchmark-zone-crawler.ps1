param(
    [string]$Zone = "GUI_STATION",
    [int]$Port = 18081,
    [int]$PollSeconds = 15,
    [int]$MaxGrids = 1,
    [int]$MaxPlaceIds = 10,
    [ValidateSet("BOTH", "LEGACY", "APOLLO_FIRST")]
    [string]$BenchmarkMode = "BOTH"
)

$ErrorActionPreference = "Stop"
$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot "..\..\..")).Path
$jarPath = (Resolve-Path (Join-Path $repoRoot "server\crawler\build\libs\crawler-0.0.1.jar")).Path
$runId = Get-Date -Format "yyyyMMdd-HHmmss"
$outputDir = Join-Path $repoRoot "server\crawler\build\benchmark\$Zone-$runId"
New-Item -ItemType Directory -Path $outputDir -Force | Out-Null

function Wait-ForCrawler([System.Diagnostics.Process]$Process, [string]$BaseUrl) {
    $deadline = (Get-Date).AddMinutes(3)
    while ((Get-Date) -lt $deadline) {
        if ($Process.HasExited) {
            throw "Crawler process exited during startup with code $($Process.ExitCode)."
        }
        try {
            $health = Invoke-RestMethod -Uri "$BaseUrl/actuator/health" -TimeoutSec 3
            if ($health.status -eq "UP") {
                return
            }
        } catch {
            Start-Sleep -Seconds 2
        }
    }
    throw "Crawler did not become healthy within 3 minutes."
}

function Stop-Crawler([System.Diagnostics.Process]$Process) {
    if ($null -eq $Process -or $Process.HasExited) {
        return
    }
    Stop-Process -Id $Process.Id
    $Process.WaitForExit(15000) | Out-Null
}

function Invoke-CrawlMode([string]$Mode, [int]$ModePort) {
    $stdoutPath = Join-Path $outputDir "$Mode.stdout.log"
    $stderrPath = Join-Path $outputDir "$Mode.stderr.log"
    $baseUrl = "http://127.0.0.1:$ModePort"
    $javaArgs = @(
        "-Dspring.profiles.active=local",
        "-jar", $jarPath,
        "--server.port=$ModePort",
        "--crawler.naver-place.detail-mode=$Mode",
        "--crawler.naver-place.max-grids=$MaxGrids",
        "--crawler.naver-place.max-place-ids=$MaxPlaceIds",
        "--spring.data.redis.database=15",
        "--streams.ai-analysis-crawling=kustaurant-benchmark-crawling",
        "--streams.ai-analysis-review=kustaurant-benchmark-review",
        "--streams.ai-analysis-dlq=kustaurant-benchmark-dlq",
        "--streams.group=kustaurant-benchmark",
        "--spring.main.banner-mode=off"
    )

    Write-Host "[$Mode] crawler starting..."
    $process = Start-Process -FilePath "java.exe" -ArgumentList $javaArgs `
        -WorkingDirectory $repoRoot -RedirectStandardOutput $stdoutPath `
        -RedirectStandardError $stderrPath -WindowStyle Hidden -PassThru

    try {
        Wait-ForCrawler -Process $process -BaseUrl $baseUrl
        Write-Host "[$Mode] crawler is healthy. Starting zone=$Zone"

        $body = @{ crawlScope = $Zone } | ConvertTo-Json
        $job = Invoke-RestMethod -Method Post -Uri "$BaseUrl/api/naver-place/crawl-zone/jobs" `
            -ContentType "application/json" -Body $body -TimeoutSec 30
        $stopwatch = [System.Diagnostics.Stopwatch]::StartNew()
        $lastProgressLine = ""

        while ($true) {
            Start-Sleep -Seconds $PollSeconds
            $status = Invoke-RestMethod -Uri "$BaseUrl/api/naver-place/crawl-zone/jobs/$($job.jobId)" -TimeoutSec 30
            $progressLine = "status=$($status.status) grid=$($status.processedGridCount)/$($status.totalGridCount) discovered=$($status.discoveredPlaceCount) attempted=$($status.attemptedPlaceCount) success=$($status.crawledSuccessCount) failed=$($status.finalFailedCount) elapsed=$([math]::Round($stopwatch.Elapsed.TotalMinutes, 1))m"
            if ($progressLine -ne $lastProgressLine) {
                Write-Host "[$Mode] $progressLine"
                $lastProgressLine = $progressLine
            }

            if ($status.status -eq "SUCCESS") {
                break
            }
            if ($status.status -eq "FAILED") {
                throw "Zone crawl failed: $($status.errorMessage)"
            }
            if ($status.status -eq "CAPTCHA_REQUIRED") {
                throw "Zone crawl paused by Naver captcha at grid=$($status.currentGrid): $($status.errorMessage)"
            }
        }

        $stopwatch.Stop()
        $payload = Invoke-RestMethod -Uri "$BaseUrl/api/naver-place/crawl-zone/jobs/$($job.jobId)/results?fromIndex=0&limit=500" -TimeoutSec 60
        $modeResult = [ordered]@{
            mode = $Mode
            zone = $Zone
            durationSeconds = [math]::Round($stopwatch.Elapsed.TotalSeconds, 3)
            status = $status
            resultCount = @($payload.results).Count
            results = @($payload.results)
        }
        $modePath = Join-Path $outputDir "$Mode.json"
        $modeResult | ConvertTo-Json -Depth 20 | Set-Content -Encoding UTF8 $modePath
        Write-Host "[$Mode] completed in $([math]::Round($stopwatch.Elapsed.TotalMinutes, 2))m; results=$(@($payload.results).Count)"
        return [pscustomobject]$modeResult
    } catch {
        Write-Host "[$Mode] failed. Recent application logs:"
        if (Test-Path $stdoutPath) {
            Get-Content $stdoutPath -Tail 80 | Write-Host
        }
        if (Test-Path $stderrPath) {
            Get-Content $stderrPath -Tail 80 | Write-Host
        }
        throw
    } finally {
        Stop-Crawler -Process $process
    }
}

function Canonical-Menus($Menus) {
    if ($null -eq $Menus) {
        return "[]"
    }
    $canonical = @($Menus | ForEach-Object {
        [ordered]@{
            menuName = $_.menuName
            menuPrice = $_.menuPrice
            menuImageUrl = $_.menuImageUrl
        }
    } | Sort-Object menuName, menuPrice, menuImageUrl)
    return ($canonical | ConvertTo-Json -Compress -Depth 5)
}

function Compare-Results($Legacy, $Apollo) {
    $legacyById = @{}
    foreach ($item in $Legacy.results) { $legacyById[$item.sourcePlaceId] = $item }
    $apolloById = @{}
    foreach ($item in $Apollo.results) { $apolloById[$item.sourcePlaceId] = $item }

    $legacyIds = @($legacyById.Keys)
    $apolloIds = @($apolloById.Keys)
    $commonIds = @($legacyIds | Where-Object { $apolloById.ContainsKey($_) } | Sort-Object)
    $onlyLegacy = @($legacyIds | Where-Object { -not $apolloById.ContainsKey($_) } | Sort-Object)
    $onlyApollo = @($apolloIds | Where-Object { -not $legacyById.ContainsKey($_) } | Sort-Object)
    $fields = @("placeName", "category", "restaurantAddress", "phoneNumber", "latitude", "longitude", "imageUrl", "crawlScope")
    $fieldDiffCounts = [ordered]@{}
    foreach ($field in $fields) { $fieldDiffCounts[$field] = 0 }
    $fieldDiffCounts["menus"] = 0
    $exactMatchCount = 0
    $mismatches = [System.Collections.Generic.List[object]]::new()

    foreach ($id in $commonIds) {
        $left = $legacyById[$id]
        $right = $apolloById[$id]
        $changedFields = [System.Collections.Generic.List[string]]::new()
        foreach ($field in $fields) {
            if ($left.$field -ne $right.$field) {
                $fieldDiffCounts[$field]++
                $changedFields.Add($field)
            }
        }
        if ((Canonical-Menus $left.menus) -ne (Canonical-Menus $right.menus)) {
            $fieldDiffCounts["menus"]++
            $changedFields.Add("menus")
        }

        if ($changedFields.Count -eq 0) {
            $exactMatchCount++
        } elseif ($mismatches.Count -lt 30) {
            $mismatches.Add([ordered]@{
                sourcePlaceId = $id
                legacyName = $left.placeName
                apolloName = $right.placeName
                changedFields = @($changedFields)
                legacyMenuCount = @($left.menus).Count
                apolloMenuCount = @($right.menus).Count
            })
        }
    }

    $speedup = if ($Apollo.durationSeconds -gt 0) {
        [math]::Round($Legacy.durationSeconds / $Apollo.durationSeconds, 3)
    } else { $null }

    return [ordered]@{
        zone = $Zone
        outputDirectory = $outputDir
        legacy = [ordered]@{
            durationSeconds = $Legacy.durationSeconds
            discovered = $Legacy.status.discoveredPlaceCount
            attempted = $Legacy.status.attemptedPlaceCount
            success = $Legacy.status.crawledSuccessCount
            failed = $Legacy.status.finalFailedCount
            resultCount = $Legacy.resultCount
        }
        apolloFirst = [ordered]@{
            durationSeconds = $Apollo.durationSeconds
            discovered = $Apollo.status.discoveredPlaceCount
            attempted = $Apollo.status.attemptedPlaceCount
            success = $Apollo.status.crawledSuccessCount
            failed = $Apollo.status.finalFailedCount
            resultCount = $Apollo.resultCount
        }
        legacyToApolloSpeedup = $speedup
        commonResultCount = $commonIds.Count
        exactMatchCount = $exactMatchCount
        exactMatchRate = if ($commonIds.Count -gt 0) { [math]::Round($exactMatchCount / $commonIds.Count, 4) } else { $null }
        onlyLegacyIds = $onlyLegacy
        onlyApolloIds = $onlyApollo
        fieldDiffCounts = $fieldDiffCounts
        mismatchExamples = @($mismatches)
    }
}

if ($BenchmarkMode -eq "BOTH") {
    $legacyResult = Invoke-CrawlMode -Mode "LEGACY" -ModePort $Port
    $apolloResult = Invoke-CrawlMode -Mode "APOLLO_FIRST" -ModePort ($Port + 1)
    $comparison = Compare-Results -Legacy $legacyResult -Apollo $apolloResult
    $comparisonPath = Join-Path $outputDir "comparison.json"
    $comparison | ConvertTo-Json -Depth 20 | Set-Content -Encoding UTF8 $comparisonPath
    Write-Host "Comparison saved to $comparisonPath"
    $comparison | ConvertTo-Json -Depth 20
} else {
    $singleResult = Invoke-CrawlMode -Mode $BenchmarkMode -ModePort $Port
    [ordered]@{
        mode = $singleResult.mode
        zone = $singleResult.zone
        durationSeconds = $singleResult.durationSeconds
        discovered = $singleResult.status.discoveredPlaceCount
        attempted = $singleResult.status.attemptedPlaceCount
        success = $singleResult.status.crawledSuccessCount
        failed = $singleResult.status.finalFailedCount
        resultCount = $singleResult.resultCount
        outputDirectory = $outputDir
    } | ConvertTo-Json -Depth 10
}
