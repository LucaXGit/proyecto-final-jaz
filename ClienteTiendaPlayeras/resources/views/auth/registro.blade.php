<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">

    <meta
        name="viewport"
        content="width=device-width, initial-scale=1.0"
    >

    <title>Crear cuenta | Metro Drop</title>

    <style>
        :root {
            --color-primary: #0b3ed1;
            --color-primary-hover: #092fa3;
            --color-accent: #f05a28;
            --color-text: #111827;
            --color-muted: #667085;
            --color-border: #d7dce5;
            --color-background: #ffffff;
            --color-error: #b42318;
        }

        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
        }

        body {
            min-height: 100vh;
            background: var(--color-background);
            color: var(--color-text);
            font-family: Arial, Helvetica, sans-serif;
        }

        .registration-layout {
            display: grid;
            grid-template-columns: 1fr 1fr;
            min-height: 100vh;
        }

        .registration-panel {
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 48px 24px;
            background: #ffffff;
        }

        .registration-container {
            width: 100%;
            max-width: 455px;
        }

        .registration-title {
            margin-bottom: 6px;
            font-size: clamp(2rem, 4vw, 3rem);
            font-weight: 900;
            line-height: 1;
            letter-spacing: -1.5px;
            text-transform: uppercase;
        }

        .registration-subtitle {
            margin-bottom: 32px;
            color: var(--color-muted);
            font-size: 0.95rem;
        }

        .form-group {
            margin-bottom: 17px;
        }

        .form-label {
            display: block;
            margin-bottom: 7px;
            color: var(--color-text);
            font-size: 0.72rem;
            font-weight: 800;
            letter-spacing: 1.2px;
            text-transform: uppercase;
        }

        .form-control {
            width: 100%;
            min-height: 49px;
            padding: 12px 14px;
            border: 1px solid var(--color-border);
            border-radius: 0;
            outline: none;
            background: #ffffff;
            color: var(--color-text);
            font-size: 0.95rem;
            transition:
                border-color 0.2s ease,
                box-shadow 0.2s ease;
        }

        .form-control::placeholder {
            color: #98a2b3;
        }

        .form-control:focus {
            border-color: var(--color-primary);
            box-shadow: 0 0 0 3px rgba(11, 62, 209, 0.12);
        }

        .form-control.is-invalid {
            border-color: var(--color-error);
        }

        .field-error {
            display: block;
            margin-top: 6px;
            color: var(--color-error);
            font-size: 0.8rem;
        }

        .submit-button {
            width: 100%;
            min-height: 52px;
            margin-top: 8px;
            border: 0;
            background: var(--color-primary);
            color: #ffffff;
            cursor: pointer;
            font-size: 0.92rem;
            font-weight: 800;
            letter-spacing: 0.4px;
            text-transform: uppercase;
            transition: background-color 0.2s ease;
        }

        .submit-button:hover {
            background: var(--color-primary-hover);
        }

        .divider {
            display: flex;
            align-items: center;
            gap: 16px;
            margin: 28px 0;
            color: var(--color-muted);
            font-size: 0.7rem;
            font-weight: 700;
            text-transform: uppercase;
        }

        .divider::before,
        .divider::after {
            content: "";
            flex: 1;
            height: 1px;
            background: var(--color-border);
        }

        .login-section {
            text-align: center;
        }

        .login-question {
            margin-bottom: 12px;
            color: var(--color-muted);
            font-size: 0.9rem;
        }

        .login-link {
            display: inline-block;
            padding-bottom: 4px;
            border-bottom: 2px solid var(--color-accent);
            color: var(--color-accent);
            font-size: 0.74rem;
            font-weight: 800;
            letter-spacing: 1px;
            text-decoration: none;
            text-transform: uppercase;
        }

        .image-panel {
            position: relative;
            min-height: 100vh;
            overflow: hidden;
            background:
                linear-gradient(
                    to bottom,
                    rgba(255, 255, 255, 0.02),
                    rgba(255, 255, 255, 0.18)
                ),
                url('{{ asset('images/playera-registro-log.jpg') }}')
                center center / cover no-repeat,
                #d4d4d4;
        }

        .brand {
            position: absolute;
            left: 48px;
            bottom: 42px;
            color: #111111;
            font-size: clamp(3rem, 7vw, 6rem);
            font-weight: 950;
            line-height: 0.85;
            letter-spacing: -5px;
            text-transform: uppercase;
        }

        .general-message {
            margin-bottom: 20px;
            padding: 12px 14px;
            border: 1px solid #f5c2c0;
            background: #fff4f3;
            color: var(--color-error);
            font-size: 0.88rem;
        }

        @media (max-width: 900px) {
            .registration-layout {
                grid-template-columns: 1fr;
            }

            .registration-panel {
                min-height: 100vh;
                padding: 40px 22px;
            }

            .image-panel {
                display: none;
            }

            .registration-container {
                max-width: 520px;
            }
        }

        @media (max-width: 480px) {
            .registration-panel {
                align-items: flex-start;
                padding-top: 48px;
            }

            .registration-title {
                font-size: 2rem;
            }
        }
    </style>
</head>

<body>
    <main class="registration-layout">
        <section class="registration-panel">
            <div class="registration-container">
                <h1 class="registration-title">
                    Crear cuenta
                </h1>

                <p class="registration-subtitle">
                    Únete a la comunidad de Metro Drop.
                </p>

                @if ($errors->any())
                    <div class="general-message">
                        Revisa los datos ingresados antes de continuar.
                    </div>
                @endif

                <form
                    method="POST"
                    action="#"
                    novalidate
                >
                    @csrf

                    <div class="form-group">
                        <label
                            for="nombre"
                            class="form-label"
                        >
                            Nombre
                        </label>

                        <input
                            id="nombre"
                            class="form-control @error('nombre') is-invalid @enderror"
                            type="text"
                            name="nombre"
                            value="{{ old('nombre') }}"
                            placeholder="Tu nombre"
                            maxlength="100"
                            autocomplete="given-name"
                            required
                        >

                        @error('nombre')
                            <span class="field-error">
                                {{ $message }}
                            </span>
                        @enderror
                    </div>

                    <div class="form-group">
                        <label
                            for="apellido"
                            class="form-label"
                        >
                            Apellido
                        </label>

                        <input
                            id="apellido"
                            class="form-control @error('apellido') is-invalid @enderror"
                            type="text"
                            name="apellido"
                            value="{{ old('apellido') }}"
                            placeholder="Tu apellido"
                            maxlength="100"
                            autocomplete="family-name"
                            required
                        >

                        @error('apellido')
                            <span class="field-error">
                                {{ $message }}
                            </span>
                        @enderror
                    </div>

                    <div class="form-group">
                        <label
                            for="correo"
                            class="form-label"
                        >
                            Correo electrónico
                        </label>

                        <input
                            id="correo"
                            class="form-control @error('correo') is-invalid @enderror"
                            type="email"
                            name="correo"
                            value="{{ old('correo') }}"
                            placeholder="tu@email.com"
                            maxlength="150"
                            autocomplete="email"
                            required
                        >

                        @error('correo')
                            <span class="field-error">
                                {{ $message }}
                            </span>
                        @enderror
                    </div>

                    <div class="form-group">
                        <label
                            for="password"
                            class="form-label"
                        >
                            Contraseña
                        </label>

                        <input
                            id="password"
                            class="form-control @error('password') is-invalid @enderror"
                            type="password"
                            name="password"
                            placeholder="Crea una contraseña"
                            minlength="8"
                            maxlength="72"
                            autocomplete="new-password"
                            required
                        >

                        @error('password')
                            <span class="field-error">
                                {{ $message }}
                            </span>
                        @enderror
                    </div>

                    <div class="form-group">
                        <label
                            for="password_confirmation"
                            class="form-label"
                        >
                            Confirmar contraseña
                        </label>

                        <input
                            id="password_confirmation"
                            class="form-control"
                            type="password"
                            name="password_confirmation"
                            placeholder="Repite tu contraseña"
                            minlength="8"
                            maxlength="72"
                            autocomplete="new-password"
                            required
                        >
                    </div>

                    <button
                        type="submit"
                        class="submit-button"
                    >
                        Registrarse
                    </button>
                </form>

                <div class="divider">
                    O
                </div>

                <div class="login-section">
                    <p class="login-question">
                        ¿Ya tienes una cuenta?
                    </p>

                    <a
                        href="#"
                        class="login-link"
                    >
                        Iniciar sesión
                    </a>
                </div>
            </div>
        </section>

        <section
            class="image-panel"
            aria-label="Playera Metro Drop"
        >
            <div class="brand">
                Metro<br>Drop
            </div>
        </section>
    </main>
</body>
</html>