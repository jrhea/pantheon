/*
 * Copyright 2018 ConsenSys AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package tech.pegasys.pantheon.ethereum.p2p.discovery;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;
import static org.junit.Assert.assertNotNull;
import static tech.pegasys.pantheon.ethereum.p2p.discovery.PeerDiscoveryTestHelper.generateKeyPairs;
import static tech.pegasys.pantheon.ethereum.p2p.discovery.PeerDiscoveryTestHelper.generatePeers;

import tech.pegasys.pantheon.crypto.SECP256K1;
import tech.pegasys.pantheon.ethereum.p2p.discovery.internal.FindNeighborsPacketData;
import tech.pegasys.pantheon.ethereum.p2p.discovery.internal.NeighborsPacketData;
import tech.pegasys.pantheon.ethereum.p2p.discovery.internal.Packet;
import tech.pegasys.pantheon.ethereum.p2p.discovery.internal.PacketData;
import tech.pegasys.pantheon.ethereum.p2p.discovery.internal.PacketType;
import tech.pegasys.pantheon.ethereum.rlp.RLP;
import tech.pegasys.pantheon.ethereum.rlp.RLPException;
import tech.pegasys.pantheon.util.bytes.BytesValue;
import tech.pegasys.pantheon.util.bytes.MutableBytesValue;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import io.vertx.core.buffer.Buffer;
import org.junit.Test;

public class PeerDiscoveryPacketSedesTest {

  @Test
  public void serializeDeserializeEntirePacket() {
    final byte[] r = new byte[64];
    new Random().nextBytes(r);
    final BytesValue target = BytesValue.wrap(r);
    final SECP256K1.KeyPair kp = SECP256K1.KeyPair.generate();

    final FindNeighborsPacketData packetData = FindNeighborsPacketData.create(target);
    final Packet packet = Packet.create(PacketType.FIND_NEIGHBORS, packetData, kp);
    final Buffer encoded = packet.encode();
    assertNotNull(encoded);

    final Packet decoded = Packet.decode(encoded);
    assertThat(decoded.getType()).isEqualTo(PacketType.FIND_NEIGHBORS);
    assertThat(decoded.getNodeId()).isEqualTo(kp.getPublicKey().getEncodedBytes());
    assertThat(decoded.getPacketData(NeighborsPacketData.class)).isNotPresent();
    assertThat(decoded.getPacketData(FindNeighborsPacketData.class)).isPresent();
  }

  @Test
  public void serializeDeserializeFindNeighborsPacketData() {
    final byte[] r = new byte[64];
    new Random().nextBytes(r);
    final BytesValue target = BytesValue.wrap(r);

    final FindNeighborsPacketData packet = FindNeighborsPacketData.create(target);
    final BytesValue serialized = RLP.encode(packet::writeTo);
    assertNotNull(serialized);

    final FindNeighborsPacketData deserialized =
        FindNeighborsPacketData.readFrom(RLP.input(serialized));
    assertThat(deserialized.getTarget()).isEqualTo(target);
    // Fuzziness: allow a skew of 1.5 seconds between the time the message was generated until the
    // assertion.
    assertThat(deserialized.getExpiration())
        .isCloseTo(
            System.currentTimeMillis() + PacketData.DEFAULT_EXPIRATION_PERIOD_MS, offset(1500L));
  }

  @Test
  public void neighborsPacketData() {
    final List<DiscoveryPeer> peers = Arrays.asList(generatePeers(generateKeyPairs(5)));

    final NeighborsPacketData packet = NeighborsPacketData.create(peers);
    final BytesValue serialized = RLP.encode(packet::writeTo);
    assertNotNull(serialized);

    final NeighborsPacketData deserialized = NeighborsPacketData.readFrom(RLP.input(serialized));
    assertThat(deserialized.getNodes()).isEqualTo(peers);
    // Fuzziness: allow a skew of 1.5 seconds between the time the message was generated until the
    // assertion.
    assertThat(deserialized.getExpiration())
        .isCloseTo(
            System.currentTimeMillis() + PacketData.DEFAULT_EXPIRATION_PERIOD_MS, offset(1500L));
  }

  @Test(expected = RLPException.class)
  public void deserializeDifferentPacketData() {
    final byte[] r = new byte[64];
    new Random().nextBytes(r);
    final BytesValue target = BytesValue.wrap(r);

    final FindNeighborsPacketData packet = FindNeighborsPacketData.create(target);
    final BytesValue serialized = RLP.encode(packet::writeTo);
    assertNotNull(serialized);

    NeighborsPacketData.readFrom(RLP.input(serialized));
  }

  @Test(expected = PeerDiscoveryPacketDecodingException.class)
  public void integrityCheckFailsUnmatchedHash() {
    final byte[] r = new byte[64];
    new Random().nextBytes(r);
    final BytesValue target = BytesValue.wrap(r);

    final SECP256K1.KeyPair kp = SECP256K1.KeyPair.generate();

    final FindNeighborsPacketData data = FindNeighborsPacketData.create(target);
    final Packet packet = Packet.create(PacketType.FIND_NEIGHBORS, data, kp);

    final BytesValue encoded = BytesValue.wrapBuffer(packet.encode());
    final MutableBytesValue garbled = encoded.mutableCopy();
    final int i = garbled.size() - 1;
    // Change one bit in the last byte, which belongs to the payload, hence the hash will not match
    // any longer.
    garbled.set(i, (byte) (garbled.get(i) + 0x01));
    Packet.decode(Buffer.buffer(garbled.extractArray()));
  }
}
