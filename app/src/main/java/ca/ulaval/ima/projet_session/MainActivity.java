package ca.ulaval.ima.projet_session;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity{
    DatabaseHelper mDatabaseHelper;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mDatabaseHelper = new DatabaseHelper(this);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.action_export_csv)
        {
            Toast.makeText(this, "File saved at : " + mDatabaseHelper.exportDB(this), Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends android.support.v4.app.FragmentStatePagerAdapter
    {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            while (getSupportFragmentManager().getBackStackEntryCount() > 0){
                getSupportFragmentManager().popBackStackImmediate();
            }
            switch (position){
                case 0:
                    RootGPS rootGPS = new RootGPS();
                    return rootGPS;
                case 1:
                    RootListeDepenses rootListeDepenses = new RootListeDepenses();
                    return rootListeDepenses;
                case 2:
                    RootGDrive rootGDrive = new RootGDrive();
                    return rootGDrive;
                case 3:
                    RootStatistiques rootStatistiques = new RootStatistiques();
                    return rootStatistiques;
                default:
                    return null;
            }
        }

        @Override
        public int getCount()
        {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            switch (position)
            {
                case 0:
                    return "GPS";
                case 1:
                    return "Dépenses";
                case 2:
                    return "GDrive";
                case 3:
                    return "Statistiques";
            }
            return null;
        }
    }
}
