package ca.ulaval.ima.projet_session;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class Adapter_Fragment_ListeDepense extends BaseAdapter
{
    private class ViewHolder
    {
        protected TextView itemName;
    }

    private ArrayList<String> mListItems;
    private LayoutInflater mLayoutInflater;

    public Adapter_Fragment_ListeDepense(Context context, ArrayList<String> arrayList)
    {
        mListItems = arrayList;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() { return mListItems.size(); }

    @Override
    public Object getItem(int position) { return mListItems.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup)
    {
        // create a ViewHolder reference
        ViewHolder holder;

        //check to see if the reused view is null or not, if is not null then reuse it
        if (view == null)
        {
            holder = new ViewHolder();

            view = mLayoutInflater.inflate(R.layout.container_liste_depenses, viewGroup, false);

            // get all views you need to handle from the cell and save them in the view holder
            holder.itemName = (TextView)view.findViewById(R.id.liste_depenses_item);

            // save the view holder on the cell view to get it back latter
            view.setTag(holder);
        }
        else
        {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)view.getTag();
        }

        //get the string item from the position "position" from array list to put it on the TextView
        String stringItem = mListItems.get(position);
        holder.itemName.setText(stringItem);

        return view;
    }
}
