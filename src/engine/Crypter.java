package engine;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;




public class Crypter {
//	<Algorithm>nazwa</Algorithm>
//	<KeySize>rozmiar klucza</KeySize>
//	<BlockSize>rozmiar bloku</BlockSize>
//	<CipherMode>TRYB</CipherMode>
//	<IV>wektor_pocz¹tkowy</IV>
//	<ApprovedUsers>
//	<User>
//	<Name>nazwa u¿ytkownika</Name>
//	<SessionKey>klucz sesyjny zaszyfrowany kluczem publicznym</SessionKey>
//	</User>
//	</ApprovedUsers>
//	<ByteComplement>

	private String algorithm;
	private int keySize;
	private int blockSize;
	private String cipherMode;
	private byte[] IV;

	
	public void Crypt() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		KeyGenerator kgen = KeyGenerator.getInstance("MARS");
		SecretKey skey = kgen.generateKey();
		//byte[] raw = skey.getEncoded();
		//SecretKeySpec skeySpec = new SecretKeySpec(raw, "MARS");

		Cipher cipher = Cipher.getInstance("MARS/ECB/NoPadding");
		cipher.init(Cipher.ENCRYPT_MODE, skey);
		//byte[] toEncrypt = generateBytesTable();
		
		byte[] toEncrypt = "SampleMessage ts".getBytes();
		
		//toEncrypt = "This is just an example".getBytes();
		byte[] encrypted = cipher.doFinal(toEncrypt);
		
		System.out.println("Bytes to encrypt: " + new String(toEncrypt, "UTF-8"));
		System.out.println("Bytes encrypted: " + new String(encrypted, "UTF-8"));
		
		//decrypt
		cipher.init(Cipher.DECRYPT_MODE, skey);
		byte[] decrypted = cipher.doFinal(encrypted);
		
		System.out.println("Bytes decrypted: " + new String(decrypted, "UTF-8"));
		System.out.println("Bytes decrypted: " + decrypted);
	}
	private byte[] generateBytesTable()	{		
	
		byte[] bTable = new byte[128];
		for(int i =0; i<128; i++) {
			bTable[i] = 0x62;
		}
		return bTable;
	}
	public String getAlgorithm() {
		return algorithm;
	}
	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}
	public int getKeySize() {
		return keySize;
	}
	public void setKeySize(int keySize) {
		this.keySize = keySize;
	}
	public int getBlockSize() {
		return blockSize;
	}
	public void setBlockSize(int blockSize) {
		this.blockSize = blockSize;
	}
	public String getCipherMode() {
		return cipherMode;
	}
	public void setCipherMode(String cipherMode) {
		this.cipherMode = cipherMode;
	}
	public byte[] getIV() {
		return IV;
	}
	public void setIV(byte[] iV) {
		IV = iV;
	}
	
	
}
