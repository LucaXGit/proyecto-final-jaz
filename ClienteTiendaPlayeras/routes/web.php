<?php

use App\Http\Controllers\AuthController;
use App\Http\Controllers\CarritoController;
use App\Http\Controllers\OrdenController;
use App\Http\Controllers\PlayerasController;
use App\Http\Controllers\UserController;
use Illuminate\Support\Facades\Route;

Route::get('/registro', [AuthController::class, 'mostrarRegistro'])
    ->name('registro');

Route::post('/registro', [AuthController::class, 'registrar'])
    ->name('registro.store');

Route::get('/login', [AuthController::class, 'mostrarLogin'])
    ->name('login');

Route::post('/login', [AuthController::class, 'login'])
    ->name('login.store');

Route::get('/', [PlayerasController::class, 'index'])
    ->name('tienda.index');

Route::middleware('usuario.auth')->group(function (): void {
    Route::post('/logout', [AuthController::class, 'logout'])
        ->name('logout');

    Route::get('/perfil', [UserController::class, 'perfil'])
        ->name('perfil');

    Route::post('/vender/{id}', [PlayerasController::class, 'vender'])
        ->name('playeras.vender');

    // Carrito de compras
    Route::get('/carrito', [CarritoController::class, 'index'])
        ->name('carrito.index');

    Route::post('/carrito/agregar', [CarritoController::class, 'agregar'])
        ->name('carrito.agregar');

    Route::put('/carrito/{id}', [CarritoController::class, 'actualizar'])
        ->name('carrito.actualizar');

    Route::delete('/carrito/{id}', [CarritoController::class, 'eliminar'])
        ->name('carrito.eliminar');

    Route::delete('/carrito', [CarritoController::class, 'vaciar'])
        ->name('carrito.vaciar');

    // Órdenes / Pedidos
    Route::post('/ordenes', [OrdenController::class, 'checkout'])
        ->name('ordenes.checkout');

    Route::get('/ordenes', [OrdenController::class, 'misOrdenes'])
        ->name('ordenes.index');

    Route::get('/ordenes/{id}', [OrdenController::class, 'detalle'])
        ->name('ordenes.detalle');

    Route::middleware('admin.auth')->group(function (): void {
        Route::get('/admin/usuarios', [UserController::class, 'adminPanel'])
            ->name('admin.usuarios');

        Route::put('/admin/usuarios/{id}/rol', [UserController::class, 'actualizarRol'])
            ->name('admin.usuarios.rol');

        Route::post('/store', [PlayerasController::class, 'store'])
            ->name('playeras.store');

        Route::put('/update/{id}', [PlayerasController::class, 'update'])
            ->name('playeras.update');

        Route::delete('/destroy/{id}', [PlayerasController::class, 'destroy'])
            ->name('playeras.destroy');
    });
});