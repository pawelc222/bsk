package engine;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;





public class Engine {
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

	private String algorithm = "MARS";
	private int keySize;
	private int blockSize;
	//private int byteComplement;
	private String cipherMode;
	private byte[] IV;
    private byte[] toEncrypt;
    private byte[] key = null;
    private byte[] toDecrypt;
    private byte[] encrypted;
    private byte[] decrypted;
    private Dictionary<String, byte[]> usersWithKeys;
    
    public Engine(String filePath, String userName) {
    	prepareForDecrypt(filePath, userName);
    }
	public Engine(String cipherMode2, int keySize2, int subBlockLength, String filePath) {
		this.cipherMode = cipherMode2;
		this.keySize = keySize2;
		this.blockSize = subBlockLength;
		this.toEncrypt = getBytesFromFile(filePath);
		
	}
	
	private void prepareForDecrypt(String filePath, String userName) {
		
		byte[] fileToSeparate = getBytesFromFile(filePath);
		
		int splitIndex = -1; 
	
		for(int i=0; i<fileToSeparate.length; i++) {
			if(fileToSeparate[i]==0x00) {
				splitIndex = i;
				break;
			}
		}
			
		byte[] header = new byte[splitIndex];
		byte[] encData = new byte[fileToSeparate.length-splitIndex-1];
		System.arraycopy(fileToSeparate, 0, header, 0, splitIndex);
		System.arraycopy(fileToSeparate, splitIndex+1, encData, 0, fileToSeparate.length-splitIndex-1);
		toDecrypt = encData;
		//System.arraycopy(header, 0, header2, 0, header.length-1);
		try {
			Files.write(Paths.get("temp.xml"), header, StandardOpenOption.CREATE);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println(e1.toString());
		}
		
	    try {
	    	 
	    	File fXmlFile = new File("temp.xml");
	    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	    	Document doc = dBuilder.parse(fXmlFile);
	    	doc.getDocumentElement().normalize();
//	     
//	    	System.out.println("keysize element :" + doc.getElementsByTagName("KeySize").item(0).getTextContent());
//	    	System.out.println("sblock element :" + doc.getElementsByTagName("SubBlock").item(0).getTextContent());
//	    	System.out.println("ciphermode element :" + doc.getElementsByTagName("CipherMode").item(0).getTextContent());
//	    	System.out.println("iv element :" + doc.getElementsByTagName("IV").item(0).getTextContent());
//
//	    	System.out.println("sessionkey element :" + doc.getElementsByTagName("SessionKey").item(0).getTextContent());
	    	
	    	
	    	for(int i=0; i<doc.getElementsByTagName("Name").getLength();i++) {
	    		if(doc.getElementsByTagName("Name").item(i).getTextContent().equals(userName)) {	    			
	    			System.out.println("user element :" + doc.getElementsByTagName("Name").item(i).getTextContent());
	    			key = DatatypeConverter.parseHexBinary(doc.getElementsByTagName("SessionKey").item(i).getTextContent());	    			
	    			break;
	    		}
	    	}
	    	if(key == null){
	    		key = DatatypeConverter.parseHexBinary(doc.getElementsByTagName("SessionKey").item(0).getTextContent());
	    	}
	    	blockSize = Integer.parseInt(doc.getElementsByTagName("SubBlock").item(0).getTextContent());
	    	keySize = Integer.parseInt(doc.getElementsByTagName("KeySize").item(0).getTextContent());
	    	IV = DatatypeConverter.parseHexBinary(doc.getElementsByTagName("IV").item(0).getTextContent()); 
	    	cipherMode = doc.getElementsByTagName("CipherMode").item(0).getTextContent();
	        } catch (Exception e) {
	    	e.printStackTrace();
	        } finally {
	        	try {
					Files.delete(Paths.get("temp.xml"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println(e.toString());
				}	
	        }
	}
	
	private static byte[] getBytesFromFile(String filePath) {
		// TODO Auto-generated method stub
		
		try {
			return Files.readAllBytes(Paths.get(filePath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.toString());
		}
		return null;
	}
	
	public void encrypt(List<User> users) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		KeyGenerator kgen = KeyGenerator.getInstance("MARS");
		kgen.init(keySize);
		SecretKey skey = kgen.generateKey();
				
		Cipher cipher = Cipher.getInstance(getMode());
		
		cipher.init(Cipher.ENCRYPT_MODE, skey);
		
		Padding();
		
		encrypted = cipher.doFinal(this.toEncrypt);
		
		//System.out.println("Bytes to encrypt: " + new String(toEncrypt, "UTF-8"));
		//System.out.println("Bytes encrypted: " + new String(encrypted, "UTF-8"));
		
		IV = cipher.getIV();
		key = skey.getEncoded();
		for(User u : users) {
			RSA rsaEngine = new RSA();
			u.encryptedKey = rsaEngine.encrypt(u, key);
		}
		
		IV = cipher.getIV();
		
	}
	public static void encryptPrivateKey(String password, String privateKeyPath ) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		
		MessageDigest sha = MessageDigest.getInstance("SHA-512");
		byte[] hash = sha.digest(password.getBytes());
		hash = Arrays.copyOf(hash, 32); // use only first 256 bit

		SecretKeySpec secretKeySpec = new SecretKeySpec(hash, "MARS");
		
		Cipher cipher = Cipher.getInstance("MARS/ECB/NoPadding");
		
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
		
		byte [] encryptedPrivateKey = cipher.doFinal(getBytesFromFile(privateKeyPath));
		
		try {
			Files.write(Paths.get(privateKeyPath + "enc"), encryptedPrivateKey, StandardOpenOption.CREATE);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println(e1.toString());
		}		
	}
	
	public static User decryptPrivateKey(String password, String privateKeyPath, String userName) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		
		MessageDigest sha = MessageDigest.getInstance("SHA-512");
		byte[] hash = sha.digest(password.getBytes());
		hash = Arrays.copyOf(hash, 32); // use only first 256 bit

		SecretKeySpec secretKeySpec = new SecretKeySpec(hash, "MARS");
				
		Cipher cipher = Cipher.getInstance("MARS/ECB/NoPadding");
		
		cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
		
		byte [] decryptedPrivateKey = cipher.doFinal(getBytesFromFile(privateKeyPath));
		String decryptedPrivateKeyPath = privateKeyPath.replaceFirst("enc", "");
		try {
			Files.write(Paths.get(decryptedPrivateKeyPath), decryptedPrivateKey, StandardOpenOption.CREATE);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println(e1.toString());
		}
		return new User(userName, decryptedPrivateKeyPath);
	}
	private byte[] hashWithSHA512(String password) {
		byte[] hash = null;
		MessageDigest md;        
        try {
            md= MessageDigest.getInstance("SHA-512");
 
            md.update(password.getBytes());
            hash = md.digest(password.getBytes());            
 
        } catch (NoSuchAlgorithmException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
        return hash;
	}
	
	private String getMode() {
		String mode = "MARS/ECB/NoPadding";
		switch(cipherMode) {
			case "CBC":
				mode = "MARS/CBC/NoPadding";
				break;
			case "OFB":
				if(this.blockSize > 0)
					mode = "MARS/OFB"+ this.blockSize + "/NoPadding";
				else
					mode = "MARS/OFB/NoPadding";
				break;
			case "CFB":
				if(this.blockSize > 0)
					mode = "MARS/CFB"+ this.blockSize + "/NoPadding";
				else
					mode = "MARS/CFB/NoPadding";
				break;
			case "ECB":
				mode = "MARS/ECB/NoPadding";
				break;
		}
		return mode;
	}
	
	private void Padding() {
		int byteComplement = 16 - (toEncrypt.length % 16);		
			
			byte[] complement = new byte[byteComplement];
			SecureRandom random = new SecureRandom();
			random.nextBytes(complement);			
			complement[byteComplement-1] = (byte)byteComplement;
			//joint arays
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
			try {
				outputStream.write(this.toEncrypt);
				outputStream.write(complement);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println(e.toString());
			}				
			toEncrypt = outputStream.toByteArray();
		
	}
	
	public void decrypt(User u) throws Exception {
		
		RSA rsaEngine = new RSA();	
		key = rsaEngine.decrypt(u, key, keySize);
		
		SecretKey skey = new SecretKeySpec(key, "MARS");
		Cipher cipher = Cipher.getInstance(getMode());
		//cipher.getIV
		IvParameterSpec ivSpec = new IvParameterSpec(IV);
		try {
			cipher.init(Cipher.DECRYPT_MODE, skey, ivSpec);
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			System.out.println(e.toString());
		}
		byte[] decryptedWithPadding = cipher.doFinal(this.toDecrypt);
		
		int decryptedLength = decryptedWithPadding.length;
		int paddingLength = (int)decryptedWithPadding[decryptedLength-1];
		decrypted = new byte[decryptedLength-paddingLength];
		System.arraycopy(decryptedWithPadding, 0, decrypted, 0, decryptedWithPadding.length-paddingLength);
		
//		System.out.println("Bytes decrypted: " + new String(decrypted, "UTF-8"));	
	
	}
	public static String toHex(byte [] buf) {
	    StringBuffer strbuf = new StringBuffer(buf.length * 2);
	    int i;
	    for (i = 0; i < buf.length; i++) {
	        if (((int) buf[i] & 0xff) < 0x10) {
	            strbuf.append("0");
	        }
	        strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
	    }
	    return strbuf.toString();
	}
	
	public void saveEncryptedToFile(String fileName, List<User> users) {
		try
		{
		  DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		  DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		  //root elements
		  Document doc = docBuilder.newDocument();

		  Element rootElement = doc.createElement("EncryptedFileHeader");
		  doc.appendChild(rootElement);


		  //algorithm elements
		  Element algorithm = doc.createElement("Algorithm");
		  algorithm.appendChild(doc.createTextNode("MARS"));
		  rootElement.appendChild(algorithm);

		  Element keySize = doc.createElement("KeySize");
		  keySize.appendChild(doc.createTextNode(Integer.toString(this.keySize)));
		  rootElement.appendChild(keySize);
		  
		  Element subBlockSize = doc.createElement("SubBlock");
		  subBlockSize.appendChild(doc.createTextNode(Integer.toString(this.blockSize)));
		  rootElement.appendChild(subBlockSize);

		  Element cipherMode = doc.createElement("CipherMode");
		  cipherMode.appendChild(doc.createTextNode(this.cipherMode));
		  rootElement.appendChild(cipherMode);
		  
		  Element elementIV = doc.createElement("IV");
		  if(this.IV != null){
			  elementIV.appendChild(doc.createTextNode(toHex(this.IV)));
		  }
		  rootElement.appendChild(elementIV);
		  
		  Element approvedUsers = doc.createElement("ApprovedUsers");
		  rootElement.appendChild(approvedUsers);		  
		  
		  for(User u : users) {
			  Element user = doc.createElement("User");
			  approvedUsers.appendChild(user);
			  
			  Element name = doc.createElement("Name");
			  name.appendChild(doc.createTextNode(u.name));
			  user.appendChild(name);
			  
			  Element sessionKey = doc.createElement("SessionKey");
			  sessionKey.appendChild(doc.createTextNode(toHex(u.encryptedKey)));
			  user.appendChild(sessionKey);			  
		  }
		  
		  //write the content into xml file
		  TransformerFactory transformerFactory = TransformerFactory.newInstance();
		  Transformer transformer = transformerFactory.newTransformer();
		  DOMSource source = new DOMSource(doc);

		  StreamResult result =  new StreamResult(new File(fileName));
		  
		  transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		  transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		  transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		  transformer.transform(source, result);

		  System.out.println("Done");

		}catch(ParserConfigurationException pce){
			System.out.println(pce.toString());
		}catch(TransformerException tfe){
			System.out.println(tfe.toString());
		}
		
		try {
			byte[] zeroByte = new byte[]{0x00};
			Files.write( Paths.get(fileName), 
                    zeroByte, 
                    StandardOpenOption.APPEND);
            Files.write( Paths.get(fileName), 
                         encrypted, 
                         StandardOpenOption.APPEND);
        }
        catch ( IOException ioe ) {
        	System.out.println(ioe.toString());
        }
		
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
	public byte[] getToEncrypt() {
		return toEncrypt;
	}
	public void setToEncrypt(byte[] toEncrypt) {
		this.toEncrypt = toEncrypt;
	}
	public byte[] getKey() {
		return key;
	}
	public void setKey(byte[] key) {
		this.key = key;
	}
	public byte[] getEncrypted() {
		return encrypted;
	}
	public void saveDecryptedToFile(String filePath) {
		// TODO Auto-generated method stub
		try {
			Files.write( Paths.get(filePath), 
			        decrypted, 
			        StandardOpenOption.CREATE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.toString());
		}
	}


	
	
}
