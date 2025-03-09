import java.io.*;
import java.util.HashMap;
import java.util.Map;

public abstract class User implements Serializable {
    private String userId;
    private String password;
    private String nickName;
    protected Map<String, Client> clientsMap;
    // 직렬화 버전을 고정
    // TODO: 직렬화 버전을 고정하는 이유를 아직 잘 모르겠음. 추후에 공부하고 추가할 것.
    private static final long serialVersionUID = 1L;
    // static으로 선언된 userMap은 프로그램 실행시 한번만 생성되고, 모든 객체가 공유한다.
    private static Map<String, User> userMap = new HashMap<>();
    // 파일경로지정 static 함으로써 객체 생성없이 사용가능
    public static final String path = "C:\\AdoptPet\\userList.txt"; // 저장할 파일 경로

    public static Map<String, User> getUserMap() {
        return userMap;
    }

    //User 생성자
    public User(String userId, String password, String nickName) {
        this.userId = userId;
        this.password = password;
        this.nickName = nickName;
    }

    // 데이터 저장 메서드 (직렬화)
    public static void save() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path))) {
            out.writeObject(userMap);
            System.out.println("사용자 데이터 저장 완료!");
        } catch (IOException e) {
            System.err.println("데이터 저장 중 오류 발생!");
            e.printStackTrace();
        }
    }

    // 데이터 로드 메서드
    public static void load() {
        File file = new File(path);
        if (!file.exists()) {
            System.out.println("기존 데이터가 없습니다. 새로운 파일을 생성합니다.");
            return;
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = in.readObject();
            if (obj instanceof HashMap) {
                userMap = (HashMap<String, User>) obj;
                System.out.println("사용자 데이터 로드 완료! 현재 등록된 계정 수: " + userMap.size());
            } else {
                System.out.println("저장된 데이터가 올바르지 않습니다.");
            }
        } catch (Exception e) {
            System.err.println("데이터 로드 중 오류 발생!");
            e.printStackTrace();
        }
    }

    // 로그인 메서드 개선
    public static void login(String userId, String password) {
        System.out.println("로그인 시도: " + userId);
        if (userMap == null || userMap.isEmpty()) {
            System.out.println("현재 등록된 사용자가 없습니다. 회원가입을 먼저 해주세요.");
            return;
        }
        User user = userMap.get(userId);
        if (!userMap.containsKey(userId)) {
            System.out.println("존재하지 않는 아이디입니다.");
        }

        if (!user.getPassword().equals(password)) {
            System.out.println("비밀번호가 일치하지 않습니다.");
        }
        System.out.println("로그인 성공!");
        // Admin 또는 Client에 따른 메뉴 실행
        if (user instanceof Admin) {
            ((Admin) user).menu();
        } else if (user instanceof Client) {
            ((Client) user).menu();
        }


    }

    // 프로그램 실행시 시행될 데이터 로드
    public static void initialize() {
        load();
        System.out.println("사용자 데이터를 불러왔습니다.");
        // 기본 Admin 계정 생성
        System.out.println("기본 Admin 계정을 확인.");

        if (!userMap.containsKey("admin")) {
            Admin defaultAdmin = new Admin("admin", "admin123", "관리자");
            userMap.put("admin", defaultAdmin);
            save();
            // 테스트 하기 위해 계정 생성 메시지 출력
            System.out.println("기본 Admin 계정이 생성되었습니다. (ID: admin, PW: admin123)");
        } else {
            System.out.println("기본 Admin 계정이 이미 존재합니다.");

        }
    }

    public abstract void menu();

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
