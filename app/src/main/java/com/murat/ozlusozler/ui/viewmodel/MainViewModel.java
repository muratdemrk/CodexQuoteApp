package com.murat.ozlusozler.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.murat.ozlusozler.R;
import com.murat.ozlusozler.data.model.Quote;
import com.murat.ozlusozler.data.repository.QuoteRepository;
import com.murat.ozlusozler.util.Event;
import com.murat.ozlusozler.util.SettingsManager;

public class MainViewModel extends AndroidViewModel {

    private final QuoteRepository repository;
    private final SettingsManager settingsManager;
    private final MutableLiveData<Quote> currentQuote = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> translating = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> favoriteState = new MutableLiveData<>(false);
    private final MutableLiveData<Event<String>> messageEvent = new MutableLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
        repository = QuoteRepository.getInstance(application);
        settingsManager = new SettingsManager(application);
    }

    public LiveData<Quote> getCurrentQuote() {
        return currentQuote;
    }

    public LiveData<Boolean> isLoading() {
        return loading;
    }

    public LiveData<Boolean> isTranslating() {
        return translating;
    }

    public LiveData<Boolean> getFavoriteState() {
        return favoriteState;
    }

    public LiveData<Event<String>> getMessageEvent() {
        return messageEvent;
    }

    public void loadInitialQuote() {
        if (currentQuote.getValue() == null) {
            loadRandomQuote();
        }
    }

    public void loadRandomQuote() {
        loading.setValue(true);
        repository.fetchRandomQuote(new QuoteRepository.QuoteResultCallback() {
            @Override
            public void onSuccess(Quote quote) {
                if (settingsManager.isAutoTranslateTurkishEnabled()) {
                    translateQuoteInternal(quote, true);
                } else {
                    currentQuote.setValue(quote);
                    loading.setValue(false);
                    updateFavoriteState(quote);
                }
            }

            @Override
            public void onError(String message) {
                loading.setValue(false);
                messageEvent.setValue(new Event<>(message));
            }
        });
    }

    public void toggleFavorite() {
        Quote quote = currentQuote.getValue();
        if (quote == null) {
            return;
        }

        repository.toggleFavorite(quote, (isFavorite, added) -> {
            favoriteState.setValue(isFavorite);
            int messageRes = added ? R.string.favorite_added : R.string.favorite_removed;
            messageEvent.setValue(new Event<>(getApplication().getString(messageRes)));
        });
    }

    public void onTranslateButtonClicked() {
        Quote quote = currentQuote.getValue();
        if (quote == null) {
            return;
        }

        if (quote.hasTranslation()) {
            if (quote.isShowingTranslated()) {
                quote.showOriginal();
            } else {
                quote.showTranslated();
            }
            currentQuote.setValue(quote);
            return;
        }

        translateQuoteInternal(quote, false);
    }

    private void translateQuoteInternal(Quote quote, boolean silentOnError) {
        translating.setValue(true);
        repository.translateQuote(quote, new QuoteRepository.TranslationCallback() {
            @Override
            public void onSuccess(Quote translatedQuote) {
                currentQuote.setValue(translatedQuote);
                translating.setValue(false);
                loading.setValue(false);
                updateFavoriteState(translatedQuote);
            }

            @Override
            public void onError(String message) {
                currentQuote.setValue(quote);
                translating.setValue(false);
                loading.setValue(false);
                updateFavoriteState(quote);
                if (!silentOnError) {
                    messageEvent.setValue(new Event<>(message));
                }
            }
        });
    }

    private void updateFavoriteState(Quote quote) {
        repository.checkIsFavorite(quote, favoriteState::setValue);
    }
}
