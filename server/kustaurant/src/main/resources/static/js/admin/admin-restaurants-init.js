document.addEventListener("DOMContentLoaded", async () => {
    await preloadAdminModals();
    await initCrawlModal();
    initializeRestaurantSyncSection();

    loadRestaurants(0);
    loadPartnerships(0);

    document.getElementById("edit-submit-btn")?.addEventListener("click", submitPartnershipEdit);
    document.getElementById("edit-cancel-btn")?.addEventListener("click", closeEditModal);
    document.getElementById("edit-modal-close")?.addEventListener("click", closeEditModal);

    document.getElementById("delete-partnerships-btn")?.addEventListener("click", openDeletePartnershipModal);
    document.getElementById("delete-partnership-submit-btn")?.addEventListener("click", deletePartnerships);
    document.getElementById("delete-partnership-cancel-btn")?.addEventListener("click", closeDeletePartnershipModal);
    document.getElementById("delete-partnership-modal-close")?.addEventListener("click", closeDeletePartnershipModal);
    document.querySelector("#delete-partnership-modal .admin-modal-overlay")
        ?.addEventListener("click", closeDeletePartnershipModal);
});
