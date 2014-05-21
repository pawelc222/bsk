package engine;

public class User {
	String name;
	String publicKeyFilePath;
	String privateKeyFilePath;
	byte [] encryptedKey;
	public byte[] getEncryptedKey() {
		return encryptedKey;
	}
	public void setEncryptedKey(byte[] encryptedKey) {
		this.encryptedKey = encryptedKey;
	}
	public User(String n, String privKeyFilePath) {
		name = n;
		privateKeyFilePath = privKeyFilePath;
		
	}
	public User(String n, String pubKeyFilePath, String privKeyFilePath) {
		name = n;
		publicKeyFilePath = pubKeyFilePath;
		privateKeyFilePath = privKeyFilePath;
		
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPublicKeyFilePath() {
		return publicKeyFilePath;
	}
	public void setPublicKeyFilePath(String publicKeyFilePath) {
		this.publicKeyFilePath = publicKeyFilePath;
	}
	public String getPrivateKeyFilePath() {
		return privateKeyFilePath;
	}
	public void setPrivateKeyFilePath(String privateKeyFilePath) {
		this.privateKeyFilePath = privateKeyFilePath;
	}
	

}
