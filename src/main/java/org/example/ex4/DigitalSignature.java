package org.example.ex4;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCSException;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;

public class DigitalSignature {

    public static void startDS() {
        Security.addProvider(new BouncyCastleProvider());

        try {
            // Загрузка закрытого ключа
            PEMParser pemParser = new PEMParser(new FileReader("private-key.pem"));
            Object keyPair = pemParser.readObject();
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
            KeyPair privateKey = converter.getKeyPair((PEMKeyPair) keyPair);
            pemParser.close();

            // Загрузка сертификата
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            FileInputStream fis = new FileInputStream("certificate.pem");
            X509Certificate certificate = (X509Certificate) cf.generateCertificate(fis);
            fis.close();

            // Подпись документа
            byte[] documentToSign = "Содержание документа".getBytes();
            byte[] signature = signDocument(documentToSign, privateKey.getPrivate(), certificate);

            // Сохранение подписи
            FileOutputStream signatureFile = new FileOutputStream("signature.bin");
            signatureFile.write(signature);
            signatureFile.close();

            System.out.println("Документ успешно подписан и подпись сохранена.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static byte[] signDocument(byte[] document, PrivateKey privateKey, X509Certificate certificate)
            throws OperatorCreationException, CertificateException, IOException, PKCSException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {

        // Подготовка запроса на подпись
        PKCS10CertificationRequestBuilder p10Builder = new PKCS10CertificationRequestBuilder(certificate.getSubjectX500Principal(), certificate.getPublicKey());
        JcaContentSignerBuilder csBuilder = new JcaContentSignerBuilder("SHA256WithRSA");
        ContentSigner signer = csBuilder.build(privateKey);
        PKCS10CertificationRequest csr = p10Builder.build(signer);

        // Подпись документа
        Signature signature = Signature.getInstance("SHA256WithRSA");
        signature.initSign(privateKey);
        signature.update(document);
        byte[] signedData = signature.sign();

        return signedData;
    }
}
