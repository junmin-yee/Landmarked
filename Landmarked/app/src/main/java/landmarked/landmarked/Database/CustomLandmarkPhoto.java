package landmarked.landmarked.Database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(foreignKeys = @ForeignKey(entity = CustomLocalLandmark.class,
        parentColumns = "CustomLandmarkID",
        childColumns = "CustomLandmarkID"))
public class CustomLandmarkPhoto
{
    @PrimaryKey
    public int PhotoID;

    @ColumnInfo(name = "CustomLandmarkID")
    public int CustomLandmarkID;

    @ColumnInfo(name = "FileName")
    public String m_filename;

    @ColumnInfo(name = "DateTaken")
    public Date m_datetaken;

    @ColumnInfo(name = "FilePath")
    public String m_filepath;
}
