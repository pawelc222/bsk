package engine;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RSA {

	public byte[] encrypt(User user, byte[] dataToEncrypt) {

		//System.out.println("\n----------------ENCRYPTION STARTED------------");

		//System.out.println("Data Before Encryption :" + dataToEncrypt);

		byte[] encryptedData = null;
		try {
			PublicKey pubKey = readPublicKeyFromFile(user.publicKeyFilePath);
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, pubKey);
			encryptedData = cipher.doFinal(dataToEncrypt);
			//System.out.println("Encryted Data: " + encryptedData);

		} catch (Exception e) {
			System.out.println(e.toString());
		}

		//System.out.println("----------------ENCRYPTION COMPLETED------------");
		return encryptedData;
	}

	public byte[] decrypt(User user, byte[] dataToDecrypt, int keySize) throws Exception {
		//System.out.println("\n----------------DECRYPTION STARTED------------");
		byte[] decryptedData = null;

		try {
			PrivateKey privateKey = readPrivateKeyFromFile(user.privateKeyFilePath);
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			decryptedData = cipher.doFinal(dataToDecrypt);
			//System.out.println("Decrypted Data: " + new String(decryptedData));

		} catch (Exception e) {
			System.out.println(e.toString());
			
		}

		System.out.println("----------------DECRYPTION COMPLETED------------");
		return decryptedData;
	}

	public void generateKeyPair(String publicKeyFilePath,
			String privateKeyFilePath, String userName, String password)
			throws NoSuchAlgorithmException, InvalidKeySpecException,
			IOException {
		System.out
				.println("-------GENRATE PUBLIC and PRIVATE KEY-------------");
	

		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(2048); // 1024 used for normal securities
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		System.out.println("Public Key - " + publicKey);
		System.out.println("Private Key - " + privateKey);

		saveKey(publicKeyFilePath, publicKey.getModulus(),
				publicKey.getPublicExponent());
		saveKey(privateKeyFilePath, privateKey.getModulus(),
				privateKey.getPrivateExponent());
		
		try {
			Engine.encryptPrivateKey(password, privateKeyFilePath);
		} catch (InvalidKeyException | NoSuchPaddingException
				| IllegalBlockSizeException | BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				Files.delete(Paths.get(privateKeyFilePath));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println(e.toString());
			}	
		}

	}

	public PublicKey readPublicKeyFromFile(String fileName) throws IOException {
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = new FileInputStream(new File(fileName));
			ois = new ObjectInputStream(fis);

			BigInteger modulus = (BigInteger) ois.readObject();
			BigInteger exponent = (BigInteger) ois.readObject();

			// Get Public Key
			RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(modulus,
					exponent);
			KeyFactory fact = KeyFactory.getInstance("RSA");
			PublicKey publicKey = fact.generatePublic(rsaPublicKeySpec);

			return publicKey;

		} catch (Exception e) {
			System.out.println(e.toString());
		} finally {
			if (ois != null) {
				ois.close();
				if (fis != null) {
					fis.close();
				}
			}
		}
		return null;
	}

	public PrivateKey readPrivateKeyFromFile(String fileName) throws IOException  {
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = new FileInputStream(new File(fileName));
			ois = new ObjectInputStream(fis);

			BigInteger modulus = (BigInteger) ois.readObject();
			BigInteger exponent = (BigInteger) ois.readObject();

			// Get Private Key
			RSAPrivateKeySpec rsaPrivateKeySpec = new RSAPrivateKeySpec(
					modulus, exponent);
			KeyFactory fact = KeyFactory.getInstance("RSA");
			PrivateKey privateKey = fact.generatePrivate(rsaPrivateKeySpec);

			return privateKey;

		} catch (Exception e) {
			System.out.println(e.toString());
		} finally {
			if (ois != null) {
				ois.close();
			}				
			if (fis != null) {
					fis.close();
			}
			
		}
		return null;
	}

	private void saveKey(String fileName, BigInteger mod, BigInteger exp)
			throws IOException {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;

		try {
			System.out.println("Generating " + fileName + "...");
			fos = new FileOutputStream(fileName);
			oos = new ObjectOutputStream(new BufferedOutputStream(fos));

			oos.writeObject(mod);
			oos.writeObject(exp);

			System.out.println(fileName + " generated successfully");
		} catch (Exception e) {
			System.out.println(e.toString());
		} finally {
			if (oos != null) {
				oos.close();

				if (fos != null) {
					fos.close();
				}
			}
		}
	}


	public void saveToFile(String fileName, BigInteger mod, BigInteger exp)
			throws IOException {
		ObjectOutputStream oout = new ObjectOutputStream(
				new BufferedOutputStream(new FileOutputStream(fileName)));
		try {
			oout.writeObject(mod);
			oout.writeObject(exp);
		} catch (Exception e) {
			System.out.println(e.toString());
		} finally {
			oout.close();
		}
	}

}
