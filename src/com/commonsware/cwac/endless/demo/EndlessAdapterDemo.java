/***
  Copyright (c) 2008-2009 CommonsWare, LLC
  
  Licensed under the Apache License, Version 2.0 (the "License"); you may
  not use this file except in compliance with the License. You may obtain
  a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/

package com.commonsware.cwac.endless.demo;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import com.commonsware.cwac.endless.demo.EndlessAdapter;
import java.util.ArrayList;

public class EndlessAdapterDemo extends ListActivity {
  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    setContentView(R.layout.main);
    
    ArrayList<Integer> items=new ArrayList<Integer>();
    
    for (int i=0;i<25;i++) { items.add(i); }
    
    setListAdapter(new DemoAdapter(items));
  }
  
  class DemoAdapter extends EndlessAdapter {
    private RotateAnimation rotate=null;
    
    DemoAdapter(ArrayList<Integer> list) {
      super(new ArrayAdapter<Integer>(EndlessAdapterDemo.this,
                                      R.layout.row,
                                      android.R.id.text1,
                                      list));
      
      rotate=new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF,
                                  0.5f, Animation.RELATIVE_TO_SELF,
                                  0.5f);
      rotate.setDuration(600);
      rotate.setRepeatMode(Animation.RESTART);
      rotate.setRepeatCount(Animation.INFINITE);
    }
    
    @Override
    protected View getPendingView(ViewGroup parent) {
      View row=getLayoutInflater().inflate(R.layout.row, null);
      
      View child=row.findViewById(android.R.id.text1);
      
      child.setVisibility(View.GONE);
      
      child=row.findViewById(R.id.throbber);
      child.setVisibility(View.VISIBLE);
      child.startAnimation(rotate);
      
      return(row);
    }
    
    @Override
    protected boolean cacheInBackground() {
      SystemClock.sleep(10000);       // pretend to do work
      
      return(getWrappedAdapter().getCount()<75);
    }
    
    @Override
    protected void appendCachedData() {
      if (getWrappedAdapter().getCount()<75) {
        @SuppressWarnings("unchecked")
        ArrayAdapter<Integer> a=(ArrayAdapter<Integer>)getWrappedAdapter();
        
        for (int i=0;i<25;i++) { a.add(a.getCount()); }
      }
    }
  }
}
