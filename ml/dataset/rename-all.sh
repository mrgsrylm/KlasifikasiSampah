#!/bin/bash

# Tentukan direktori sumber tempat file-file berada
direktori_sumber="./kategori_sampah/organik"

# Tentukan direktori tujuan tempat file-file yang telah diubah nama akan dipindahkan
direktori_tujuan="./kategori_sampah/organik_new"

# Tentukan awalan nama file baru
awalan_nama_file="organik"

# Inisialisasi variabel angka
angka=1

# Loop melalui semua file di direktori sumber
for file in "$direktori_sumber"/*; do
    # Periksa apakah file adalah file biasa
    if [ -f "$file" ]; then
        # Ekstrak ekstensi file
        ekstensi="${file##*.}"
        
        # Bentuk nama file baru dengan menambahkan angka berurutan dan ekstensi
        nama_file_baru="$awalan_nama_file$angka.$ekstensi"
        
        # Periksa apakah nama file baru sudah ada, jika ya, tambahkan angka ke nama file
        while [ -e "$direktori_tujuan/$nama_file_baru" ]; do
            angka=$((angka + 1))
            nama_file_baru="$awalan_nama_file$angka.$ekstensi"
        done

        # Ubah nama file dan pindahkan ke direktori tujuan
        mv "$file" "$direktori_tujuan/$nama_file_baru"
        
        # Tambahkan 1 ke variabel angka untuk file berikutnya
        angka=$((angka + 1))
    fi
done

echo "Pemrosesan selesai. File-file telah diubah namanya dan dipindahkan ke direktori baru."