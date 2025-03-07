
public class Admin extends User {
	Admin(String userId, String password) {
		super(userId,password);
//		super.setUserId("admin");
//		super.setPassword("admin");
//		super.setNickName("admin");
	}
	
	
	
	
	public void deleteUser() {
		
	}
	
	public void createBoard() {
		
	}
	
	public void deleteBoard() {
		
	}
	
	public void searchUser() {
		
	}

	@Override
	public void menu() {
		System.out.println("**********"+getUserId()+" sir welcom "+"**********");
		System.out.println("Admin menu ");
		System.out.println("1. user list");
		System.out.println("2. user search");
	}
	
	
}
