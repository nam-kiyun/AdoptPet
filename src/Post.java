import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Post {
	private int postNum;
	private String title;
	private String content;
	private String author;
	private LocalDateTime createAt;
	private LocalDateTime updateDate;
	private String userId;
	private Map<Integer, Comment> commentsMap;
	private int commentNumCount = 1;

	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

	public Post(int postNum, String title, String content, String author, LocalDateTime createAt,
			LocalDateTime updateDate, Map<Integer, Comment> commentsMap) {
		this.postNum = postNum;
		this.title = title;
		this.content = content;
		this.author = author;
		this.createAt = createAt;
		this.updateDate = updateDate;
		this.userId = Client.nowUserId;
		this.commentsMap = new HashMap<Integer, Comment>();
	}

	// 댓글 달기
	public void writeComment() {
		int commentNum = commentNumCount++;
		System.out.println("댓글을 입력하세요.");
		String str=null;
		try {
			str = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Comment comment = new Comment(commentNum, str, this.author, LocalDateTime.now(), LocalDateTime.now());
		commentsMap.put(comment.getCommentNum(), comment);
		System.out.println("댓글이 작성되었습니다.");
	}

	// 댓글 수정
	public void editComment(){
		Set<Integer> set = commentsMap.keySet();
		System.out.println("댓글 번호를 입력해주세요.");
		int n=0;
		try {
			n = Integer.parseInt(br.readLine());
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (Integer num : set) {
			if (n == num) {
				if (this.userId.equals(commentsMap.get(num).getUserId()))
					System.out.println("댓글을 새로 입력해주세요");
				try {
					commentsMap.get(num).setContent(br.readLine());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("댓글이 수정되었습니다.");
	}

	// 댓글 삭제
	public void deleteComment() {
		Set<Integer> set = commentsMap.keySet();
		Iterator<Integer> it = set.iterator();
		System.out.println("댓글 번호를 입력해주세요.");
		int n=0;
		try {
			n = Integer.parseInt(br.readLine());
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while (it.hasNext()) {
			int num = it.next();
			if (n == num) {
				it.remove();
			}
		}
		System.out.println("선택하신 댓글이 삭제되었습니다.");
	}
	
	public void commentsSave() {
		String path="C:\\AdoptPet\\cat\\Comments.txt";
		
		FileOutputStream fos = null;
		BufferedOutputStream bos =null;
		ObjectOutputStream oos = null;
		
		try {
			fos = new FileOutputStream(new File(path));
			bos = new BufferedOutputStream(fos);
			oos = new ObjectOutputStream(bos);
			
			oos.writeObject(commentsMap);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				oos.close();
				bos.close();
				fos.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
	}

	public void commentRun() {
		
		File file = new File(author);
		while (true) {
			String input = null;
			System.out.println("1.댓글 작성\t2.댓글 수정\t3.댓글 삭제\t0.종료");
			try {
				input = br.readLine();
			} catch (Exception e) {
				e.printStackTrace();
			}
			switch (input) {
			case "1":
				writeComment();
				break;
			case "2":
				editComment();
				break;
			case "3":
				deleteComment();
				break;
			case "0":
				commentsSave();
				return;
			}
		}
	}

	public int getPostNum() {
		return postNum;
	}

	public void setPostNum(int postNum) {
		this.postNum = postNum;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public LocalDateTime getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(LocalDateTime updateDate) {
		this.updateDate = updateDate;
	}

	public Map<Integer, Comment> getCommentsMap() {
		return commentsMap;
	}

	public void setCommentsMap(Map<Integer, Comment> commentsMap) {
		this.commentsMap = commentsMap;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
