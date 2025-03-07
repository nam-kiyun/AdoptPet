import java.time.LocalDateTime;
import java.io.*;
import java.util.*;

public class Client extends User {
	private int wrongCount;
	private LocalDateTime banTime;
	private Map<String, Client> clientsMap;

	public void register() {
		
	}
	
	public void editProfile() {
		
	}
	
	public void deleteAccount() {
		
	}
	
	static void showUserList() {
		
	}

	@Override
	public void menu() {
		// TODO Auto-generated method stub
		
	}
	
	public int getWrongCount() {
		return wrongCount;
	}

	public void setWrongCount(int wrongCount) {
		this.wrongCount = wrongCount;
	}

	public LocalDateTime getBanTime() {
		return banTime;
	}

	public void setBanTime(LocalDateTime banTime) {
		this.banTime = banTime;
	}

	public Map<String, Client> getClientsMap() {
		return clientsMap;
	}

	public void setClientsMap(Map<String, Client> clientsMap) {
		this.clientsMap = clientsMap;
	}
}
