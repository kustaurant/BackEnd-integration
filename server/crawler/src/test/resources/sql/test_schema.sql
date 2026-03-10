ALTER TABLE ai_analysis_job
    ADD CONSTRAINT fk_ai_analysis_job_restaurant_id
        FOREIGN KEY (restaurant_id)
            REFERENCES restaurant(restaurant_id);

ALTER TABLE ai_analysis_review
    ADD CONSTRAINT fk_review_analysis_restaurant_id
        FOREIGN KEY (restaurant_id)
            REFERENCES restaurant(restaurant_id);

ALTER TABLE ai_analysis_review
    ADD CONSTRAINT fk_review_analysis_job_id
        FOREIGN KEY (job_id)
            REFERENCES ai_analysis_job(id);

ALTER TABLE ai_summary
    ADD CONSTRAINT fk_restaurant_ai_summary_restaurant_id
        FOREIGN KEY (restaurant_id)
            REFERENCES restaurant(restaurant_id);

ALTER TABLE ai_summary
    ADD CONSTRAINT fk_restaurant_ai_summary_job_id
        FOREIGN KEY (last_job_id)
            REFERENCES ai_analysis_job(id);