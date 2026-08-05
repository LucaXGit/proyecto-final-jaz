<?php

namespace App\Http\Controllers;

use App\Services\PayaraClient;
use Illuminate\Http\RedirectResponse;
use Illuminate\Http\Request;
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
                'min:4',
                'max:72',
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

        $respuesta = PayaraClient::request('POST', $this->usuariosApiUrl . '/register', [
            'nombre' => trim($datosValidados['nombre']),
            'apellido' => trim($datosValidados['apellido']),
            'correo' => strtolower(trim($datosValidados['correo'])),
            'password' => $datosValidados['password'],
        ]);

        if ($respuesta['success']) {
            return redirect()
                ->route('tienda.index')
                ->with(
                    'success',
                    $respuesta['message'] ?? 'Usuario registrado correctamente.'
                );
        }

        if ($respuesta['code'] === 409) {
            return back()
                ->withInput($request->except([
                    'password',
                    'password_confirmation',
                ]))
                ->withErrors([
                    'correo' => $respuesta['message'] ?? 'El correo electrónico ya está registrado.',
                ]);
        }

        if ($respuesta['code'] === 400 && !empty($respuesta['errors'])) {
            return back()
                ->withInput($request->except([
                    'password',
                    'password_confirmation',
                ]))
                ->withErrors($respuesta['errors']);
        }

        return back()
            ->withInput($request->except([
                'password',
                'password_confirmation',
            ]))
            ->withErrors([
                'general' => $respuesta['message'] ?? 'No fue posible registrar al usuario.',
            ]);
    }

    public function mostrarLogin(): View
    {
        return view('auth.login');
    }

    public function login(Request $request): RedirectResponse
    {
        $datosValidados = $request->validate([
            'correo' => [
                'required',
                'email',
                'max:150',
            ],
            'password' => [
                'required',
                'string',
                'max:72',
            ],
            'recordarme' => [
                'nullable',
                'boolean',
            ],
        ], [
            'correo.required' => 'El correo electrónico es obligatorio.',
            'correo.email' => 'El formato del correo electrónico no es válido.',
            'correo.max' => 'El correo electrónico no puede superar los 150 caracteres.',

            'password.required' => 'La contraseña es obligatoria.',
            'password.max' => 'La contraseña no puede superar los 72 caracteres.',

            'recordarme.boolean' => 'El valor de recordarme no es válido.',
        ]);

        $respuesta = PayaraClient::request('POST', $this->usuariosApiUrl . '/login', [
            'correo' => strtolower(trim($datosValidados['correo'])),
            'password' => $datosValidados['password'],
        ]);

        if ($respuesta['success']) {
            $usuario = $respuesta['usuario'] ?? null;

            if (!$this->usuarioRespuestaValido($usuario)) {
                return back()
                    ->withInput($request->except('password'))
                    ->withErrors([
                        'general' => 'El servidor devolvió una respuesta de autenticación inválida.',
                    ]);
            }

            $request->session()->regenerate();

            $request->session()->put('usuario', [
                'id' => $usuario['id'],
                'nombre' => $usuario['nombre'],
                'apellido' => $usuario['apellido'],
                'correo' => $usuario['correo'],
                'rol' => $usuario['rol'],
                'token' => $respuesta['token'] ?? null,
            ]);

            $request->session()->put(
                'recordarme',
                $request->boolean('recordarme')
            );

            return redirect()
                ->route('tienda.index')
                ->with(
                    'success',
                    $respuesta['message'] ?? 'Inicio de sesión exitoso.'
                );
        }

        if ($respuesta['code'] === 401) {
            return back()
                ->withInput($request->except('password'))
                ->withErrors([
                    'correo' => $respuesta['message'] ?? 'El correo electrónico o la contraseña son incorrectos.',
                ]);
        }

        if ($respuesta['code'] === 400 && !empty($respuesta['errors'])) {
            return back()
                ->withInput($request->except('password'))
                ->withErrors($respuesta['errors']);
        }

        return back()
            ->withInput($request->except('password'))
            ->withErrors([
                'general' => $respuesta['message'] ?? 'No fue posible iniciar sesión.',
            ]);
    }

    public function logout(Request $request): RedirectResponse
    {
        $request->session()->invalidate();
        $request->session()->regenerateToken();

        return redirect()
            ->route('login')
            ->with('success', 'Sesión cerrada correctamente.');
    }

    private function usuarioRespuestaValido(mixed $usuario): bool
    {
        if (!is_array($usuario)) {
            return false;
        }

        $camposRequeridos = [
            'id',
            'nombre',
            'apellido',
            'correo',
            'rol',
        ];

        foreach ($camposRequeridos as $campo) {
            if (!array_key_exists($campo, $usuario)) {
                return false;
            }
        }

        return is_numeric($usuario['id'])
            && is_string($usuario['nombre'])
            && is_string($usuario['apellido'])
            && is_string($usuario['correo'])
            && is_string($usuario['rol']);
    }
}