<?php

namespace App\Http\Controllers;

use Illuminate\Http\Client\ConnectionException;
use Illuminate\Http\RedirectResponse;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Http;
use Illuminate\Validation\Rules\Password;
use Illuminate\View\View;

class AuthController extends Controller
{
    private string $usuariosApiUrl;

    public function __construct()
    {
        $this->usuariosApiUrl = rtrim(
            (string) config('services.usuarios.url'),
            '/'
        );
    }

    public function mostrarRegistro(): View
    {
        return view('auth.registro');
    }

    public function registrar(Request $request): RedirectResponse
    {
        $datosValidados = $request->validate([
            'nombre' => [
                'required',
                'string',
                'min:2',
                'max:100',
            ],
            'apellido' => [
                'required',
                'string',
                'min:2',
                'max:100',
            ],
            'correo' => [
                'required',
                'email',
                'max:150',
            ],
            'password' => [
                'required',
                'confirmed',
                'max:72',
                Password::min(8)
                    ->mixedCase()
                    ->numbers(),
            ],
        ], [
            'nombre.required' => 'El nombre es obligatorio.',
            'nombre.min' => 'El nombre debe contener al menos 2 caracteres.',
            'nombre.max' => 'El nombre no puede superar los 100 caracteres.',

            'apellido.required' => 'El apellido es obligatorio.',
            'apellido.min' => 'El apellido debe contener al menos 2 caracteres.',
            'apellido.max' => 'El apellido no puede superar los 100 caracteres.',

            'correo.required' => 'El correo electrónico es obligatorio.',
            'correo.email' => 'El formato del correo electrónico no es válido.',
            'correo.max' => 'El correo electrónico no puede superar los 150 caracteres.',

            'password.required' => 'La contraseña es obligatoria.',
            'password.confirmed' => 'Las contraseñas no coinciden.',
            'password.max' => 'La contraseña no puede superar los 72 caracteres.',
        ]);

        try {
            $response = Http::acceptJson()
                ->asJson()
                ->timeout(10)
                ->post($this->usuariosApiUrl, [
                    'nombre' => trim($datosValidados['nombre']),
                    'apellido' => trim($datosValidados['apellido']),
                    'correo' => strtolower(trim($datosValidados['correo'])),
                    'password' => $datosValidados['password'],
                ]);
        } catch (ConnectionException $exception) {
            report($exception);

            return back()
                ->withInput($request->except([
                    'password',
                    'password_confirmation',
                ]))
                ->withErrors([
                    'general' => 'No fue posible conectar con el servidor de usuarios.',
                ]);
        }

        $respuesta = $response->json();

        if ($response->successful()) {
    return redirect()
        ->route('tienda.index')
        ->with(
            'success',
            $respuesta['message']
                ?? 'Usuario registrado correctamente.'
        );
}
        if ($response->status() === 409) {
            return back()
                ->withInput($request->except([
                    'password',
                    'password_confirmation',
                ]))
                ->withErrors([
                    'correo' => $respuesta['message']
                        ?? 'El correo electrónico ya está registrado.',
                ]);
        }

        if ($response->status() === 400) {
            $erroresBackend = $respuesta['errors'] ?? [];

            if (is_array($erroresBackend) && !empty($erroresBackend)) {
                return back()
                    ->withInput($request->except([
                        'password',
                        'password_confirmation',
                    ]))
                    ->withErrors($erroresBackend);
            }
        }

        return back()
            ->withInput($request->except([
                'password',
                'password_confirmation',
            ]))
            ->withErrors([
                'general' => $respuesta['message']
                    ?? 'No fue posible registrar al usuario.',
            ]);
    }
}