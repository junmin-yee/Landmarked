package landmarked.landmarked;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(foreignKeys = @ForeignKey(entity = CustomLocalLandmark.class,
            parentColumns = "CustomLandmarkID",
            childColumns = "CustomLandmarkID"))
public class CustomLandmarkNotes
{
    @PrimaryKey
    public int NoteID;

    @ColumnInfo(name = "CustomLandmarkID")
    public int CustomLandmarkID;

    @ColumnInfo(name = "Title")
    public String m_title;

    @ColumnInfo(name = "Text")
    public String m_text;

    @ColumnInfo(name = "LastEdited")
    public Date m_lastedited;
}