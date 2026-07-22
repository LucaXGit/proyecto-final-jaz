<?php

use App\Http\Controllers\AuthController;
use App\Http\Controllers\PlayerasController;
use Illuminate\Support\Facades\Route;

Route::get('/registro', [AuthController::class, 'mostrarRegistro'])
    ->name('registro');

Route::get('/', [PlayerasController::class, 'index'])
    ->name('tienda.index');

Route::post('/store', [PlayerasController::class, 'store'])
    ->name('playeras.store');

Route::put('/update/{id}', [PlayerasController::class, 'update'])
    ->name('playeras.update');

Route::delete('/destroy/{id}', [PlayerasController::class, 'destroy'])
    ->name('playeras.destroy');

Route::post('/vender/{id}', [PlayerasController::class, 'vender'])
    ->name('playeras.vender');