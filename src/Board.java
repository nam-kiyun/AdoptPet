import java.util.Map;

public class Board {
	private int CategoryNum;
	private int CategoryName;
	private Map<Integer,Post> postsMap;
	
	public void writePost() {
		
	}
	
	public void editPost() {
		
	}

	public void deletePost() {
		
	}
	
	public void searchPost() {
		
	}
	
	public void sortPost() {
		
	}
	
	public int getCategoryNum() {
		return CategoryNum;
	}

	public void setCategoryNum(int categoryNum) {
		CategoryNum = categoryNum;
	}

	public int getCategoryName() {
		return CategoryName;
	}

	public void setCategoryName(int categoryName) {
		CategoryName = categoryName;
	}

	public Map<Integer, Post> getPostsMap() {
		return postsMap;
	}

	public void setPostsMap(Map<Integer, Post> postsMap) {
		this.postsMap = postsMap;
	}
}
