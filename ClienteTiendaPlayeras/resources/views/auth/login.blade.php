<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">

    <meta
        name="viewport"
        content="width=device-width, initial-scale=1.0"
    >

    <title>Iniciar sesión | Metro Drop</title>

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

        .login-layout {
            display: grid;
            grid-template-columns: 1fr 1fr;
            min-height: 100vh;
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

        .login-panel {
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 48px 24px;
            background: #ffffff;
        }

        .login-container {
            width: 100%;
            max-width: 455px;
        }

        .login-title {
            margin-bottom: 6px;
            font-size: clamp(2rem, 4vw, 3rem);
            font-weight: 900;
            line-height: 1;
            letter-spacing: -1.5px;
            text-transform: uppercase;
        }

        .login-subtitle {
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

        .login-options {
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: 16px;
            margin: 4px 0 22px;
        }

        .remember-option {
            display: inline-flex;
            align-items: center;
            gap: 9px;
            color: var(--color-muted);
            cursor: pointer;
            font-size: 0.86rem;
        }

        .remember-option input {
            width: 17px;
            height: 17px;
            accent-color: var(--color-primary);
            cursor: pointer;
        }

        .forgot-link {
            color: var(--color-accent);
            font-size: 0.82rem;
            font-weight: 700;
            text-decoration: none;
        }

        .forgot-link:hover {
            text-decoration: underline;
        }

        .submit-button {
            width: 100%;
            min-height: 52px;
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

        .register-section {
            text-align: center;
        }

        .register-question {
            margin-bottom: 12px;
            color: var(--color-muted);
            font-size: 0.9rem;
        }

        .register-link {
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

        .general-message {
            margin-bottom: 20px;
            padding: 12px 14px;
            border: 1px solid #f5c2c0;
            background: #fff4f3;
            color: var(--color-error);
            font-size: 0.88rem;
        }

        .success-message {
            margin-bottom: 20px;
            padding: 12px 14px;
            border: 1px solid #a6d9b3;
            background: #effbf2;
            color: #176b2c;
            font-size: 0.88rem;
        }

        @media (max-width: 900px) {
            .login-layout {
                grid-template-columns: 1fr;
            }

            .image-panel {
                display: none;
            }

            .login-panel {
                min-height: 100vh;
                padding: 40px 22px;
            }

            .login-container {
                max-width: 520px;
            }
        }

        @media (max-width: 480px) {
            .login-panel {
                align-items: flex-start;
                padding-top: 48px;
            }

            .login-title {
                font-size: 2rem;
            }

            .login-options {
                align-items: flex-start;
                flex-direction: column;
                gap: 12px;
            }
        }
    </style>
</head>

<body>
    <main class="login-layout">
        <section
            class="image-panel"
            aria-label="Playera Metro Drop"
        >
            <div class="brand">
                Metro<br>Drop
            </div>
        </section>

        <section class="login-panel">
            <div class="login-container">
                <h1 class="login-title">
                    Login
                </h1>

                <p class="login-subtitle">
                    Accede a tu cuenta de Metro Drop.
                </p>

                @if ($errors->has('general'))
                    <div class="general-message">
                        {{ $errors->first('general') }}
                    </div>
                @endif

                @if (session('success'))
                    <div class="success-message">
                        {{ session('success') }}
                    </div>
                @endif

                <form
                    method="POST"
                    action="{{ route('login.store') }}"
                    novalidate
                >
                    @csrf

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
                            autofocus
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
                            placeholder="Ingresa tu contraseña"
                            maxlength="72"
                            autocomplete="current-password"
                            required
                        >

                        @error('password')
                            <span class="field-error">
                                {{ $message }}
                            </span>
                        @enderror
                    </div>

                    <div class="login-options">
                        <label class="remember-option">
                            <input
                                type="checkbox"
                                name="recordarme"
                                value="1"
                                @checked(old('recordarme'))
                            >

                            <span>
                                Recordarme
                            </span>
                        </label>

                        <a
                            href="#"
                            class="forgot-link"
                            aria-disabled="true"
                            onclick="return false;"
                        >
                            ¿Olvidaste tu contraseña?
                        </a>
                    </div>

                    <button
                        type="submit"
                        class="submit-button"
                    >
                        Log in
                    </button>
                </form>

                <div class="divider">
                    O
                </div>

                <div class="register-section">
                    <p class="register-question">
                        ¿Todavía no tienes una cuenta?
                    </p>

                    <a
                        href="{{ route('registro') }}"
                        class="register-link"
                    >
                        Crear una cuenta
                    </a>
                </div>
            </div>
        </section>
    </main>
</body>
</html>