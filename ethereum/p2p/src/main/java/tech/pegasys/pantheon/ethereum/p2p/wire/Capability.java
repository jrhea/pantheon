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
package tech.pegasys.pantheon.ethereum.p2p.wire;

import static tech.pegasys.pantheon.util.bytes.BytesValue.wrap;

import tech.pegasys.pantheon.ethereum.rlp.RLPInput;
import tech.pegasys.pantheon.ethereum.rlp.RLPOutput;
import tech.pegasys.pantheon.util.bytes.BytesValues;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Represents a client capability.
 *
 * @see <a href=
 *     "https://github.com/ethereum/wiki/wiki/%C3%90%CE%9EVp2p-Wire-Protocol#p2p">Capability wire
 *     format</a>
 */
public class Capability {
  private final String name;
  private final int version;

  private Capability(final String name, final int version) {
    this.name = name;
    this.version = version;
  }

  public static Capability create(final String name, final int version) {
    return new Capability(name, version);
  }

  public String getName() {
    return name;
  }

  public int getVersion() {
    return version;
  }

  public void writeTo(final RLPOutput out) {
    out.startList();
    out.writeBytesValue(wrap(getName().getBytes(StandardCharsets.US_ASCII)));
    out.writeUnsignedByte(getVersion());
    out.endList();
  }

  public static Capability readFrom(final RLPInput in) {
    in.enterList();
    final String name = in.readBytesValue(BytesValues::asString);
    final int version = in.readUnsignedByte();
    in.leaveList();
    return Capability.create(name, version);
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final Capability that = (Capability) o;
    return version == that.version && Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, version);
  }

  @Override
  public String toString() {
    return name + "/" + version;
  }
}
