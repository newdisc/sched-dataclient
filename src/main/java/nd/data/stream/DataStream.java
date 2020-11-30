package nd.data.stream;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataStream implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(DataStream.class);
    protected StringToInputStream iStream;
    protected Reader reader;

    public Stream<String> streamLines() {
        final BufferedReader br = setReader();
        return br.lines();
    }
    
    public static List<Integer> adaptInterestIndex(List<String> colsInterest, Map<String, Integer> hdrToIdx){
    	return colsInterest
    			.stream()
    			.map(hdr -> hdrToIdx.get(hdr))
    			.collect(Collectors.toList());
    }
    public static Stream<List<String> > adaptInterestHeader(List<Integer> colsInterest, Stream<List<String>> original) {
    	return original.map(line -> {
    		int lsz = line.size();
    		return colsInterest.stream().map(idx -> {
    			if (null == idx || idx > lsz || idx < 0) {
    				logger.debug("Out of range index: {}, {}", idx, line);
    				return null;
    			}
    			return line.get(idx);
    		})
    		.collect(Collectors.toList());
    	});
    }

    public <T> List<T> createBeans(final Class<T> typeClass) {
        final HeaderColumnNameMappingStrategy<T> hcnms = new HeaderColumnNameMappingStrategy<>();
        hcnms.setType(typeClass);

        try (final BufferedReader br = setReader();) {
            return new CsvToBeanBuilder<T>(br).withMappingStrategy(hcnms).build().parse();
        } catch (final IOException e) {
            final String msg = "Issue reading Beans " + iStream.getURI();
            logger.error(msg, e);
        }
        return new ArrayList<>();
    }

    public static boolean streamToOutputStream(final Stream<String> sts, final OutputStream os) {
        sts.forEach(ln -> {
            try {
                os.write(ln.getBytes());
                os.write("\n".getBytes());
                // sts.close(); // Note: close has no effect on the forEach loop - no early
                // exits
            } catch (final IOException e) {
                logger.error("Could NOT write to Stream", e);
                throw new RuntimeException("Error Writing to stream", e);
            }
        });
        return true;
    }

    private BufferedReader setReader() {
        final InputStreamReader is = new InputStreamReader(iStream.getInputStream());
        final BufferedReader br = new BufferedReader(is);
        reader = br;
        return br;
    }

    @Override
    public void close() throws IOException {
        if (null != reader) {
            reader.close();
        }
        if (null != iStream) {
            iStream.close();
        }
    }

    public DataStream(final StringToInputStream is) {
        iStream = is;
    }
    public static void printTrustedKeys() {
    	final String SSLTS = "javax.net.ssl.trustStore";
    	final String SSLTSP = "javax.net.ssl.trustStorePassword";
    	String trustfile = System.getProperty(SSLTS);
    	if (null == trustfile) {
    		trustfile = System.getProperty("java.home") + 
    				"/lib/security/cacerts".replace('/', File.separatorChar);
    	}
    	String trustpass = System.getProperty(SSLTSP);
    	if (null == trustpass) {
    		trustpass = "changeit";
    	}
        // Load the JDK's cacerts keystore file
        try (FileInputStream is = new FileInputStream(trustfile);) {
            
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(is, trustpass.toCharArray());

            // This class retrieves the most-trusted CAs from the keystore
            PKIXParameters params = new PKIXParameters(keystore);

            // Get the set of trust anchors, which contain the most-trusted CA certificates
            Iterator<TrustAnchor> it = params.getTrustAnchors().iterator();
            while( it.hasNext() ) {
                TrustAnchor ta = it.next();
                // Get certificate
                X509Certificate cert = ta.getTrustedCert();
                
                logger.info("Certificate Name: {}", cert.getSubjectX500Principal().getName());
            }
        } catch (CertificateException | KeyStoreException | IOException |
        		NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            logger.error("Could NOT read trust store", e);
        }     	
    }
    public static void printKeyStore() {
    	final String SSLKS = "javax.net.ssl.keyStore";
    	final String SSLKSP = "javax.net.ssl.keyStorePassword";
    	String keyfile = System.getProperty(SSLKS);
    	if (null == keyfile) {
    		keyfile = System.getProperty("java.home") + 
    				"/lib/security/cacerts".replace('/', File.separatorChar);
    	}
    	String keypass = System.getProperty(SSLKSP);
    	if (null == keypass) {
    		keypass = "changeit";
    	}
        // Load the JDK's cacerts keystore file
        try (FileInputStream is = new FileInputStream(keyfile);) {
        	KeyStore keyStore = KeyStore.getInstance("JKS");//.jks file
        	char[] cakp = keypass.toCharArray();
        	/*
try{
    KeyStore keyStore = KeyStore.getInstance("Windows-MY");
    keyStore.load(null, null);  // Load keystore
} catch (Exception ex){
    ex.printStackTrace();
}
        	 * */
        	keyStore.load(is, cakp);
        	Enumeration<String> es = keyStore.aliases();
        	String alias = "";
        	while (es.hasMoreElements()) {
        		alias = (String) es.nextElement();
        		// if alias refers to a private key break at that point
        		// as we want to use that certificate
        		if (!keyStore.isKeyEntry(alias)) {
        			logger.info("NOT a private key: {}", alias );
        			continue;
        		}
        		//KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(alias,
        		//		new KeyStore.PasswordProtection(cakp));
        		//PrivateKey myPrivateKey = pkEntry.getPrivateKey();
        		// Load certificate chain
        		Certificate[] chain = keyStore.getCertificateChain(alias);
        		X509Certificate pkcert = (X509Certificate)chain[0];

        		logger.info("Found Private Key: {}", pkcert.getSubjectX500Principal().getName());
			}
        } catch (KeyStoreException | NoSuchAlgorithmException |  
        		CertificateException | IOException e) {
            logger.error("Could NOT read trust store", e);
        }
    }
}
