package net.consensys.pantheon.crypto;

import net.consensys.pantheon.util.bytes.Bytes32;
import net.consensys.pantheon.util.bytes.BytesValue;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/** Various utilities for providing hashes (digests) of arbitrary data. */
public abstract class Hash {
  private Hash() {}

  static {
    Security.addProvider(new BouncyCastleProvider());
  }

  public static final String KECCAK256_ALG = "KECCAK-256";

  private static final String SHA256_ALG = "SHA-256";
  private static final String RIPEMD160 = "RIPEMD160";

  /**
   * Helper method to generate a digest using the provided algorithm.
   *
   * @param input The input bytes to produce the digest for.
   * @param alg The name of the digest algorithm to use.
   * @return A digest.
   */
  private static byte[] digestUsingAlgorithm(final byte[] input, final String alg) {
    MessageDigest digest;
    try {
      digest = BouncyCastleMessageDigestFactory.create(alg);
      digest.update(input);
      return digest.digest();
    } catch (final NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Helper method to generate a digest using the provided algorithm.
   *
   * @param input The input bytes to produce the digest for.
   * @param alg The name of the digest algorithm to use.
   * @return A digest.
   */
  private static byte[] digestUsingAlgorithm(final BytesValue input, final String alg) {
    MessageDigest digest;
    try {
      digest = BouncyCastleMessageDigestFactory.create(alg);
      input.update(digest);
      return digest.digest();
    } catch (final NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Digest using SHA2-256.
   *
   * @param input The input bytes to produce the digest for.
   * @return A digest.
   */
  public static Bytes32 sha256(final BytesValue input) {
    return Bytes32.wrap(digestUsingAlgorithm(input, SHA256_ALG));
  }

  /**
   * Digest using keccak-256.
   *
   * @param input The input bytes to produce the digest for.
   * @return A digest.
   */
  public static Bytes32 keccak256(final BytesValue input) {
    return Bytes32.wrap(digestUsingAlgorithm(input, KECCAK256_ALG));
  }

  /**
   * Digest using RIPEMD-160.
   *
   * @param input The input bytes to produce the digest for.
   * @return A digest.
   */
  public static BytesValue ripemd160(final BytesValue input) {
    return BytesValue.wrap(digestUsingAlgorithm(input, RIPEMD160));
  }
}