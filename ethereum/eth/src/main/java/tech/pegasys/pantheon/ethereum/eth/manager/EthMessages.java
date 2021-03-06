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
package tech.pegasys.pantheon.ethereum.eth.manager;

import tech.pegasys.pantheon.util.Subscribers;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class EthMessages {
  private final Map<Integer, Subscribers<MessageCallback>> listenersByCode =
      new ConcurrentHashMap<>();

  void dispatch(final EthMessage message) {
    final Subscribers<MessageCallback> listeners = listenersByCode.get(message.getData().getCode());
    if (listeners == null) {
      return;
    }

    listeners.forEach(callback -> callback.exec(message));
  }

  public long subscribe(final int messageCode, final MessageCallback callback) {
    return listenersByCode
        .computeIfAbsent(messageCode, key -> new Subscribers<>())
        .subscribe(callback);
  }

  public void unsubscribe(final long listenerId) {
    for (final Entry<Integer, Subscribers<MessageCallback>> entry : listenersByCode.entrySet()) {
      if (entry.getValue().unsubscribe(listenerId)) {
        break;
      }
    }
  }

  @FunctionalInterface
  public interface MessageCallback {
    void exec(EthMessage message);
  }
}
