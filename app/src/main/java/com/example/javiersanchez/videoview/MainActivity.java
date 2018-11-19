package com.example.javiersanchez.videoview;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    /*Mirar :
    -https://developer.android.com/guide/components/intents-common?hl=es-419
    -https://developer.android.com/training/permissions/requesting?hl=es-419
    */

    static final int VENGO_DE_LA_CAMARA = 1;
    static final int VENGO_DE_LA_CAMARA_CON_FICHERO = 2;
    static final int PEDI_PERMISOS_DE_ESCRITURA = 2;
    Button captura;
    VideoView videoView;
    String rutaVideoActual;
    MediaController mediaController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        captura = findViewById(R.id.button);
        videoView= findViewById(R.id.videoView);
        mediaController= new MediaController(MainActivity.this);
        mediaController.setAnchorView(videoView);

        /*captura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent haceVideo = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (haceVideo.resolveActivity(getPackageManager())!=null){
                    startActivityForResult(haceVideo,VENGO_DE_LA_CAMARA);
                }else{
                    Toast.makeText(MainActivity.this, "Necesito un programa de hacer vídeos.", Toast.LENGTH_SHORT).show();
                }


            }
        });*/
        captura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pedirPermisoParaEscribirYHacerVideo();


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if ((requestCode == VENGO_DE_LA_CAMARA) && (resultCode == RESULT_OK)){
            Toast.makeText(this, "Cancelaste el vídeo", Toast.LENGTH_SHORT).show();

        }else if ((requestCode == VENGO_DE_LA_CAMARA_CON_FICHERO) && (resultCode == RESULT_OK)){
            //He hecho la foto y la he guardado, así que la foto estará en  rutaFotoActual
            videoView.setVideoURI(Uri.parse(rutaVideoActual));
            videoView.setMediaController(mediaController);
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    //mp.setLooping(true);
                    mediaController.show();
                }
            });

        }

    }
    public void capturarVideo( ) {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        File ficheroVideo = null;
        try {
            ficheroVideo = crearFicheroVideo();
            rutaVideoActual=ficheroVideo.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(ficheroVideo));
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,5);
        intent.putExtra(MediaStore.EXTRA_FINISH_ON_COMPLETION, true);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, VENGO_DE_LA_CAMARA_CON_FICHERO);
        }else{
            Toast.makeText(this, "No tengo programa o cámara", Toast.LENGTH_SHORT).show();
        }
    }
    File crearFicheroVideo() throws IOException {
        String fechaYHora = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nombreFichero = "Ejemplo_"+fechaYHora;
        File carpetaParaVideos = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
        File video = File.createTempFile(nombreFichero, ".mp4", carpetaParaVideos);
        rutaVideoActual = video.getAbsolutePath();
        return video;
    }


    void pedirPermisoParaEscribirYHacerVideo(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Aquí puedo explicar para qué quiero el permiso

            } else {

                // No explicamos nada y pedimos el permiso

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PEDI_PERMISOS_DE_ESCRITURA);

                // El resultado de la petición se recupera en onRequestPermissionsResult
            }
        }else{//Tengo los permisos
            capturarVideo();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PEDI_PERMISOS_DE_ESCRITURA: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Tengo los permisos: hago la foto:

                    this.capturarVideo();

                } else {

                    //No tengo permisos: Le digo que no se puede hacer nada
                    Toast.makeText(this, "Sin permisos de escritura no puedo guardar la imagen en alta resolución.", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            //Pondría aquí más "case" si tuviera que pedir más permisos.
        }
    }
}

