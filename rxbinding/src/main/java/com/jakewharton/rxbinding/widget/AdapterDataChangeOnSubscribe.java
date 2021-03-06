package com.jakewharton.rxbinding.widget;

import android.database.DataSetObserver;
import android.widget.Adapter;
import com.jakewharton.rxbinding.internal.MainThreadSubscription;
import rx.Observable;
import rx.Subscriber;

import static com.jakewharton.rxbinding.internal.Preconditions.checkUiThread;

final class AdapterDataChangeOnSubscribe<T extends Adapter>
    implements Observable.OnSubscribe<T> {
  private final T adapter;

  public AdapterDataChangeOnSubscribe(T adapter) {
    this.adapter = adapter;
  }

  @Override public void call(final Subscriber<? super T> subscriber) {
    checkUiThread();

    final DataSetObserver observer = new DataSetObserver() {
      @Override public void onChanged() {
        if (!subscriber.isUnsubscribed()) {
          subscriber.onNext(adapter);
        }
      }
    };
    adapter.registerDataSetObserver(observer);

    subscriber.add(new MainThreadSubscription() {
      @Override protected void onUnsubscribe() {
        adapter.unregisterDataSetObserver(observer);
      }
    });

    // Emit initial value.
    subscriber.onNext(adapter);
  }
}
