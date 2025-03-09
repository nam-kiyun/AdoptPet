import java.io.Serializable;
import java.util.Map;
import java.util.Scanner;

public class Admin extends User implements Serializable {
    private static final long serialVersionUID = 1L;

    public Admin(String userId, String password) {
        super(userId, password);
    }

    public void searchUser(String userId) {
        Map<String, User> userMap = User.getUserMap();
        if (userMap.containsKey(userId)) {
            User user = userMap.get(userId);
            System.out.println("아이디: " + user.getUserId() + ", 닉네임: " +
                    (user.getNickName() != null ? user.getNickName() : "N/A"));
        } else {
            System.out.println("등록된 사용자가 없십니다");
        }
    }

    public void deleteUser(String userId) {
        Map<String, User> userMap = User.getUserMap();
        if (userMap.containsKey(userId)) {
            userMap.remove(userId);
            User.save(); // 삭제 후 자동 저장
            System.out.println(userId + " 사용자가 삭제되었습니다.");
        } else {
            System.out.println("삭제할 사용자가 존재하지 않습니다.");
        }
    }

    public void showUsersList() {
        Map<String, User> userMap = User.getUserMap();
        System.out.println("전체 사용자 목록:");

        if (userMap.isEmpty()) {
            System.out.println("등록된 사용자가 없습니다.");
            return;
        }

        for (Map.Entry<String, User> entry : userMap.entrySet()) {
            System.out.println("아이디: " + entry.getKey() +
                    ", 닉네임: " + (entry.getValue().getNickName() != null ? entry.getValue().getNickName() : "N/A"));
        }
    }

    @Override
    public void menu() {
        while (true) {
            System.out.println("\n======== [Admin 메뉴] ========");
            System.out.println("1. 전체 사용자 목록 보기");
            System.out.println("2. 사용자 검색");
            System.out.println("3. 사용자 삭제");
            System.out.println("0. 로그아웃");
            System.out.print("선택 >> ");
            Scanner scanner = new Scanner(System.in);
            int choice = scanner.nextInt();
            scanner.nextLine(); // 버퍼 클리어

            switch (choice) {
                case 1:
                    showUsersList();
                    break;
                case 2:
                    System.out.print("검색할 아이디 입력: ");
                    String searchId = scanner.nextLine();
                    searchUser(searchId);
                    break;
                case 3:
                    System.out.print("🗑 삭제할 아이디 입력: ");
                    String deleteId = scanner.nextLine();
                    deleteUser(deleteId);
                    break;
                case 0:
                    System.out.println("로그아웃 되었습니다.");
                    return;
                default:
                    System.out.println("잘못된 입력입니다. 다시 선택하세요.");
            }
        }
    }
}
