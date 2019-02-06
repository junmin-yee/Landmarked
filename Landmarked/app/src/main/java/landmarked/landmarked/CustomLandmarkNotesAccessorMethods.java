package landmarked.landmarked;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

@Dao
public interface CustomLandmarkNotesAccessorMethods {
    @Query("Select NoteID From CustomLandmarkNotes")
    int CustomLM_getNoteID();

    @Query("SELECT CustomLandmarkID FROM CustomLandmarkNotes")
    int CustomLM_getCMLandmarkID();

    @Query("SELECT Title FROM CustomLandmarkNotes")
    String CustomLM_getTitle();

    @Query("SELECT Text FROM CustomLandmarkNotes")
    String CustomLM_getText();

    @Query("SELECT LastEdited FROM CustomLandmarkNotes")
    long CustomLM_getLastEdited();
}
