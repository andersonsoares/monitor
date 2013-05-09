
public class TeteString {

	
	public static void main(String[] args) {
		String s1 = "RT rafinhabastos RT blablabla RT dasda RT RTsdasd";
		
		s1 = s1.replaceAll("RT", " ");
		System.out.println(s1);
	}
}
