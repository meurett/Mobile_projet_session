package ca.ulaval.ima.projet_session;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Depense implements Parcelable {
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getPrix() {
        return prix;
    }

    public void setPrix(String prix) {
        this.prix = prix;
    }

    private String date;
    private String categorie;
    private String prix;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.date);
        dest.writeString(this.categorie);
        dest.writeString(this.prix);
    }

    public Depense() {
    }

    protected Depense(Parcel in) {
        this.date = in.readString();
        this.categorie = in.readString();
        this.prix = in.readString();
    }

    public static final Creator<Depense> CREATOR = new Creator<Depense>() {
        @Override
        public Depense createFromParcel(Parcel source) {
            return new Depense(source);
        }

        @Override
        public Depense[] newArray(int size) {
            return new Depense[size];
        }
    };
}