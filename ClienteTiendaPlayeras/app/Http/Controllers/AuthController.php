<?php

namespace App\Http\Controllers;

use Illuminate\View\View;

class AuthController extends Controller
{
    /**
     * Muestra el formulario para registrar un usuario.
     */
    public function mostrarRegistro(): View
    {
        return view('auth.registro');
    }
}