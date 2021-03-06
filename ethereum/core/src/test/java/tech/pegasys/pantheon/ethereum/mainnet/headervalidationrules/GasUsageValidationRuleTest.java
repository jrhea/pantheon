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
package tech.pegasys.pantheon.ethereum.mainnet.headervalidationrules;

import static org.assertj.core.api.Assertions.assertThat;

import tech.pegasys.pantheon.ethereum.core.BlockHeader;
import tech.pegasys.pantheon.ethereum.core.BlockHeaderTestFixture;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class GasUsageValidationRuleTest {

  @Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(
        new Object[][] {
          {5, 6, true}, // gasUsed is less than gasLimit is valid
          {5, 5, true}, // gasUsed is the same as gaslimit is valid
          {5, 4, false}, // gasUsed is less than gasLimit
        });
  }

  @Parameter public long gasUsed;

  @Parameter(1)
  public long gasLimit;

  @Parameter(2)
  public boolean expectedResult;

  @Test
  public void test() {
    final GasUsageValidationRule uut = new GasUsageValidationRule();
    final BlockHeaderTestFixture blockBuilder = new BlockHeaderTestFixture();

    blockBuilder.gasLimit(gasLimit);
    blockBuilder.gasUsed(gasUsed);

    final BlockHeader header = blockBuilder.buildHeader();

    assertThat(uut.validate(header, null)).isEqualTo(expectedResult);
  }
}
