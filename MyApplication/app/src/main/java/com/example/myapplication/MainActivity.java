package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    ImageView imgView; //>> 이미지뷰

    String imageUrl="http://kiokahn.synology.me:30000/"; //>> 접속할 url
    Bitmap bmImg = null; //>> 불러온 이미지를 저장할 비트맵
    CLoadImage task; //>> AsyncTask 클래스

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgView = (ImageView) findViewById(R.id.imgView);
        task = new CLoadImage();
    }

    // 이미지를 불러오기 위한 함수
    public void onClickForLoad(View v)
    {
        // AsyncTask 동작
        task.execute(imageUrl+"uploads/-/system/appearance/logo/1/Gazzi_Labs_CI_type_B_-_big_logo.png");

        Toast.makeText(getApplicationContext(), "Load", Toast.LENGTH_LONG).show();
    }

    // 이미지를 저장하기 위한 함수
    public void onClickForSave(View v)
    {
        // 비트맵 저장 함수
        saveBitmaptoJpeg(bmImg, "DCIM", "image");

        Toast.makeText(getApplicationContext(), "Save", Toast.LENGTH_LONG).show();
    }

    // 안드로이드에서 네트워크 작업을 하기 위해서는 Thread나 AsyncTask가 필요하다
    // 그래서 CLoadImage를 만들어 AsyncTask를 extend
    private class CLoadImage extends AsyncTask<String, Integer,Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {
            // TODO Auto-generated method stub
            try{
                URL myFileUrl = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();

                // url에서 이미지파일을 가지고 옴
                InputStream is = conn.getInputStream();

                // 비트맵 형식으로 변환
                bmImg = BitmapFactory.decodeStream(is);

            } catch (IOException e) {
                e.printStackTrace();
            }

            // 여기서 바로 imgView를 바꾸려고 한다면 에러가 발생
            return bmImg;
        }

        // 메인 thread에서 실행되는 onPostExecute를 통해 imgView를 바꾼다.
        @Override
        protected void onPostExecute(Bitmap img) {
            imgView.setImageBitmap(img);
        }
    }

    public static void saveBitmaptoJpeg(Bitmap bitmap,String folder, String name){
        String ex_storage = Environment.getExternalStorageDirectory().getAbsolutePath(); //>> 최상위 절대 경로
        String foler_name = "/"+folder+"/"; //>> 폴더 이름
        String file_name = name+".png"; //>> 저장할 파일이름
        String string_path = ex_storage+foler_name;
        Log.d("경로",string_path);

        File file_path;
        file_path = new File(string_path);

        //>> 경로상에 폴더가 없으면 폴더생성
        if(!file_path.exists()){
            file_path.mkdirs();
        }

        try{
            // 최종 경로 입력
            FileOutputStream out = new FileOutputStream(string_path+file_name);

            // 저장
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();

        }catch(FileNotFoundException exception){
            Log.e("FileNotFoundException", exception.getMessage());
        }catch(IOException exception){
            Log.e("IOException", exception.getMessage());
        }
    }
}