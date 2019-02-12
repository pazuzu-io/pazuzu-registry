package io.pazuzu.registry.model;

public class Review {

    private ReviewStatus reviewStatus;

    public ReviewStatus getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(ReviewStatus reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    public Review() {
    }

    public Review(ReviewStatus reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    public enum ReviewStatus {
        approved("approved"),
        declined("declined"),
        pending("pending");

        private final String jsonValue;

        ReviewStatus(String jsonValue) {
            this.jsonValue = jsonValue;
        }

        public String jsonValue() {
            return jsonValue;
        }

    }
}
