import java.io.Serializable;
import java.time.LocalDateTime;

public class Comment implements Serializable {
	private static int commentCounter = 1; // 메인 댓글 카운터
	private static int replyCounter = 1; // 대댓글 카운터
	private int commentNum; // 메인 댓글 번호 (1, 2, 3...)
	private int parentCommentNum; // 부모 댓글 번호 (대댓글일 경우 저장됨, 일반 댓글이면 0)
	private String content;
	private String author;
	private String userId;
	private LocalDateTime createAt;

	public Comment(int commentNum, String content, String author, LocalDateTime createAt) {
		this.commentNum = commentNum;
		this.parentCommentNum = 0;
		this.content = content;
		this.author = author;
		this.userId = Client.getNowUserId();
		this.createAt = createAt;
	}

	public Comment(int commentNum, int parentCommentNum, String content, String author, LocalDateTime createAt) {
		if (parentCommentNum == 0) { // 메인 댓글
			this.commentNum = commentCounter;
		} else { // 대댓글
			this.commentNum = commentNum; // 대댓글은 1-1, 1-2처럼 표현 가능
		}
		this.parentCommentNum = parentCommentNum;
		this.content = content;
		this.author = author;
		this.userId = Client.getNowUserId();
		this.createAt = createAt;
	}

	public static int getCommentCounter() {
		return commentCounter;
	}

	public static void setCommentCounter(int commentCounter) {
		Comment.commentCounter = commentCounter;
	}

	public static int getReplyCounter() {
		return replyCounter;
	}

	public static void setReplyCounter(int replyCounter) {
		Comment.replyCounter = replyCounter;
	}

	public int getCommentNum() {
		return commentNum;
	}

	public void setCommentNum(int commentNum) {
		this.commentNum = commentNum;
	}

	public int getParentCommentNum() {
		return parentCommentNum;
	}

	public void setParentCommentNum(int parentCommentNum) {
		this.parentCommentNum = parentCommentNum;
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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public LocalDateTime getCreateAt() {
		return createAt;
	}

	public void setCreateAt(LocalDateTime createAt) {
		this.createAt = createAt;
	}

	@Override
	public String toString() {
		return "Comment [commentNum=" + commentNum + ", parentCommentNum=" + parentCommentNum + ", content=" + content
				+ "]";
	}

}
