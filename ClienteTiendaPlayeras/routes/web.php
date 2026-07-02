<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\PlayerasController;

// Rutas del CRUD asignadas a cada botón
Route::get('/', [PlayerasController::class, 'index']);
Route::post('/store', [PlayerasController::class, 'store']);
Route::put('/update/{id}', [PlayerasController::class, 'update']);
Route::delete('/destroy/{id}', [PlayerasController::class, 'destroy']);
Route::post('/vender/{id}', [PlayerasController::class, 'vender']);