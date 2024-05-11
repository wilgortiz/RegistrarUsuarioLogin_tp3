package com.wilgon.registrarusuariologin.ui.registro;

import static android.app.Activity.RESULT_OK;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.wilgon.registrarusuariologin.modelo.Usuario;
import com.wilgon.registrarusuariologin.request.ApiClient;
import com.wilgon.registrarusuariologin.ui.login.MainActivity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class RegistroActivityViewModel extends AndroidViewModel {
    private Context context;
    private MutableLiveData<Usuario> mUsuario;
    private MutableLiveData<Bitmap> mFoto;

    public RegistroActivityViewModel(@NonNull Application application) {
        super(application);
        context = application.getApplicationContext();
    }


    //LIVE DATAS
    public LiveData<Bitmap> getMFoto() {
        if (mFoto == null) {
            mFoto = new MutableLiveData<>();
        }
        return mFoto;
    }

    public LiveData<Usuario> getMUsuario() {
        if (mUsuario == null) {
            mUsuario = new MutableLiveData<>();
        }
        return mUsuario;
    }


    public void LeerUsuario() {
        Usuario u = ApiClient.leer(context);
        if (u != null) {
            mUsuario.setValue(u);
        }
    }

    public void GuardarUsuario(String dni, String apellido, String nombre, String email, String password) {
        Usuario u = new Usuario(Long.parseLong(dni), apellido, nombre, email, password);
        ApiClient.guardar(context, u);
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }



/*
    //para la foto. queda la imagen acostada
    public void fotoCamara(int requestCode, int resultCode, @Nullable Intent data, int REQUEST_IMAGE_CAPTURE) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //recuperamos los datos que vienen de la camara
            Bundle extras = data.getExtras();
            //casteamos a bitmap
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

            //convertimos a un arreglo de bytes los datos de la imagen
            byte[] b = baos.toByteArray();

            File archivo = new File(context.getFilesDir(), "foto.png");
            if (archivo.exists()) {
                archivo.delete();
            }

            try {
                FileOutputStream fos = new FileOutputStream(archivo);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                bos.write(b);
                bos.flush();
                bos.close();
            } catch (IOException e) {
                       Toast.makeText(context, "Error al guardar la imagen", Toast.LENGTH_LONG).show();

            }
            mFoto.setValue(imageBitmap);
        }
    }
*/


    public void fotoCamara(int requestCode, int resultCode, @Nullable Intent data, int REQUEST_IMAGE_CAPTURE) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            // Rotar la imagen 90 grados porque la toma acostada a la foto con la camara
            //podemos implementar lo mismo para ambas camaras
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap rotatedBitmap = Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.getWidth(), imageBitmap.getHeight(), matrix, true);

            // Guardar la imagen rotada
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b = baos.toByteArray();

            File archivo = new File(context.getFilesDir(), "foto.png");
            if (archivo.exists()) {
                archivo.delete();
            }

            try {
                FileOutputStream fos = new FileOutputStream(archivo);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                bos.write(b);
                bos.flush();
                bos.close();
            } catch (IOException e) {
                Toast.makeText(context, "Error al guardar la imagen", Toast.LENGTH_LONG).show();
            }

            mFoto.setValue(rotatedBitmap);
        }
    }

    public void LeerFoto(String nombre) {
        File archivo = new File(context.getFilesDir(), nombre);

        try {
            FileInputStream fis = new FileInputStream(archivo);
            BufferedInputStream bis = new BufferedInputStream(fis);

            byte bytes[];
            bytes = new byte[bis.available()];
            bis.read(bytes);

            Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            mFoto.setValue(bm);
            bis.close();
            fis.close();
        } catch (FileNotFoundException e) {
            Toast.makeText(context, "Archivo no encontrado", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(context, "Error al leer la imagen", Toast.LENGTH_LONG).show();
        }
    }
}

