public class AnimalAsciiArt {
	// ANSI 컬러 코드
	public static final String RESET = "\033[0m"; // 색상 초기화
	public static final String RED = "\033[31m"; // 빨강
	public static final String GREEN = "\033[32m"; // 초록
	public static final String YELLOW = "\033[33m"; // 노랑
	public static final String BLUE = "\033[34m"; // 파랑
	public static final String PURPLE = "\033[35m"; // 보라
	public static final String CYAN = "\033[36m"; // 청록
	public static final String BOLD = "\033[1m"; // 굵은 글씨

	// ✅ 콘솔에서 출력하는 메서드
	public static void display() {
		final int LINE_LENGTH = 75; // 전체 라인 길이

		// 📌 입양 관리 시스템 시작
		System.out.println("=".repeat(LINE_LENGTH));
		String title = BOLD + "입양 관리 시스템  🏠" + RESET;
		System.out.printf("%" + ((LINE_LENGTH + title.length()) / 2) + "s\n", title);
		System.out.println("=".repeat(LINE_LENGTH));

		// 🏠 집 모양 + 강아지 + 고양이 (컬러 리본 🎀 추가)
		// 강아지 + 고양이 + 집 모양
		// 강아지 집 + 강아지 + 고양이 + 고양이 집
		// 🐶 강아지 + 🐱 고양이 중앙 배치 (리본 포함)
		String dogCatArt = "                   / \\__           /\\_/\\        A   D   O   P   T   \n"
				+ "                  (    @\\____     ( o.o )      ------------------   \n"
				+ "                  /         O      > ^ <      |  ADOPT A PET!  |   \n"
				+ "                 /   (_____ /     /  |  \\     | 유기동물에게 새집을 |   \n"
				+ "                 \\  /            \\     /      ------------------   \n"
				+ "                  | |            (_____)                          \n";

		System.out.println(dogCatArt);

		// 🏠 집 모양 + 강아지 + 고양이 (컬러 리본 🎀 추가)
//        String dogCatArt =
//                GREEN + "        / \\__           " + YELLOW + "    /\\_/\\   \n" +
//                GREEN + "       (    @\\____      " + YELLOW + "   ( o.o )  \n" +
//                GREEN + "       /         O      " + YELLOW + "    > ^ <   \n" +
//                GREEN + "      /   (_____ /     " + YELLOW + "   /  |  \\  \n" +
//                RED   + "      \\  /   🎀       " + PURPLE + "   \\  🎀  /  \n" +
//                GREEN + "       | |            " + YELLOW + "   (_____)   \n" + RESET +
//                
//                // 🏠 집 모양 추가
//                BLUE + "         /\\       \n" +
//                BLUE + "        /  \\      \n" +
//                BLUE + "       /____\\     \n" +
//                BLUE + "      |  🏠  |    \n" +
//                BLUE + "      |______|    \n" + RESET;

//        System.out.println(dogCatArt);

		// 📢 유기동물에게 새집을~!
//		System.out.println("-".repeat(LINE_LENGTH) + RESET);
//		String adoptionMessage = BOLD + "유기동물에게 새집을~!" + RESET;
//		System.out.printf("%" + ((LINE_LENGTH + adoptionMessage.length()) / 2) + "s\n", adoptionMessage);

		// ✅ 강조된 버튼 스타일
//        System.out.println(BLUE + "=".repeat(LINE_LENGTH) + RESET);
//        String startButton = BOLD + "🎉 [ 입양 관리 시스템 시작하기 ] 🎉" + RESET;
//        System.out.printf("%" + ((LINE_LENGTH + startButton.length()) / 2) + "s\n", startButton);
//		System.out.println("=".repeat(LINE_LENGTH) + RESET);
	}
}
