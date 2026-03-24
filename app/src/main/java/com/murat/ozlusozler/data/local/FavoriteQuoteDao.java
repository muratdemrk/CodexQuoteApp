package com.murat.ozlusozler.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FavoriteQuoteDao {

    @Query("SELECT * FROM favorite_quotes ORDER BY addedAt DESC")
    LiveData<List<FavoriteQuoteEntity>> getAllFavorites();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(FavoriteQuoteEntity entity);

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_quotes WHERE originalText = :originalText AND author = :author)")
    boolean exists(String originalText, String author);

    @Query("DELETE FROM favorite_quotes WHERE originalText = :originalText AND author = :author")
    int deleteByQuoteAndAuthor(String originalText, String author);

    @Query("DELETE FROM favorite_quotes WHERE id = :id")
    int deleteById(long id);
}
