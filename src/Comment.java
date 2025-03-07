import java.time.LocalDateTime;

public class Comment {
	private int CommentNum;
	private String content;
	private String author;
	private LocalDateTime createAt;
	
	public int getCommentNum() {
		return CommentNum;
	}
	public void setCommentNum(int commentNum) {
		CommentNum = commentNum;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public LocalDateTime getCreateAt() {
		return createAt;
	}
	public void setCreateAt(LocalDateTime createAt) {
		this.createAt = createAt;
	}
	
	
}
