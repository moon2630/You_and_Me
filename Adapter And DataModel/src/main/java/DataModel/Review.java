package DataModel;

import java.util.ArrayList;
import java.util.List;

public class Review {
    private String reviewId;
    private String userId;
    private String userName;
    private String productId;
    private String productStar;
    private String comment;
    private List<String> reviewImages;
    private int thumbUpNumber; // Added for like count
    private int thumbDownNumber; // Added for dislike count
    private List<String> likedBy; // Added to track users who liked
    private List<String> dislikedBy; // Added to track users who disliked

    // Existing getters and setters
    public String getReviewId() { return reviewId; }
    public void setReviewId(String reviewId) { this.reviewId = reviewId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getProductStar() { return productStar; }
    public void setProductStar(String productStar) { this.productStar = productStar; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public List<String> getReviewImages() { return reviewImages; }
    public void setReviewImages(List<String> reviewImages) { this.reviewImages = reviewImages; }

    // New getters and setters for like/dislike
    public int getThumbUpNumber() { return thumbUpNumber; }
    public void setThumbUpNumber(int thumbUpNumber) { this.thumbUpNumber = thumbUpNumber; }

    public int getThumbDownNumber() { return thumbDownNumber; }
    public void setThumbDownNumber(int thumbDownNumber) { this.thumbDownNumber = thumbDownNumber; }

    public List<String> getLikedBy() { return likedBy != null ? likedBy : new ArrayList<>(); }
    public void setLikedBy(List<String> likedBy) { this.likedBy = likedBy; }

    public List<String> getDislikedBy() { return dislikedBy != null ? dislikedBy : new ArrayList<>(); }
    public void setDislikedBy(List<String> dislikedBy) { this.dislikedBy = dislikedBy; }
}