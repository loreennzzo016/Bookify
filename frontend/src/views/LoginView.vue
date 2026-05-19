<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowRight, BookOpen, KeyRound, LockKeyhole, UserRound, UsersRound } from '@lucide/vue'
import { useAuthStore } from '../stores/auth'

type AccessProfile = 'admin' | 'user'

const router = useRouter()
const auth = useAuthStore()
const selectedProfile = ref<AccessProfile | null>(null)
const username = ref('')
const password = ref('')
const error = ref('')
const loading = ref(false)

function selectProfile(profile: AccessProfile) {
  selectedProfile.value = profile
  username.value = profile === 'admin' ? 'admin' : 'user'
  password.value = profile === 'admin' ? 'adminpass' : 'userpass'
}

async function submit() {
  error.value = ''
  loading.value = true
  try {
    await auth.login(username.value, password.value)
    await router.push('/dashboard')
  } catch {
    error.value = 'No se pudo iniciar sesion. Revisa las credenciales o el estado del servidor.'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <main class="min-h-screen bg-[#fafafa] text-slate-950 flex flex-col justify-center items-center p-6 md:p-12 relative selection:bg-slate-950 selection:text-white antialiased">
    
    <div class="absolute top-0 right-1/4 w-[600px] h-[400px] bg-emerald-500/[0.03] rounded-full blur-[140px] pointer-events-none"></div>
    <div class="absolute bottom-12 left-1/4 w-[500px] h-[500px] bg-blue-500/[0.02] rounded-full blur-[160px] pointer-events-none"></div>

    <div class="w-full max-w-6xl mx-auto grid gap-12 lg:gap-16 items-center lg:grid-cols-[1fr_440px] xl:grid-cols-[1.1fr_460px] relative z-10">

      <section class="flex flex-col gap-10 p-2 text-left">
        
        <nav class="flex items-center gap-3">
          <span class="flex h-10 w-10 items-center justify-center rounded-xl bg-slate-950 font-sans font-black text-white shadow-[0_4px_12px_rgba(0,0,0,0.1)]">
            B
          </span>
          <div>
            <p class="text-base font-bold tracking-tight text-slate-950">Bookify</p>
            <p class="text-[10px] font-medium uppercase text-slate-400 tracking-[0.15em]">Sistemas de Gestión</p>
          </div>
        </nav>

        <div class="max-w-xl">
          <h1 class="text-4xl sm:text-5xl font-black leading-[1.1] text-slate-950 tracking-[-0.03em]">
            La infraestructura digital para tu <span class="text-transparent bg-clip-text bg-gradient-to-r from-slate-950 via-slate-800 to-emerald-600">biblioteca</span>.
          </h1>
          <p class="mt-4 text-base text-slate-500 font-medium leading-relaxed max-w-lg">
            Control de activos, flujos de préstamos en tiempo real y analíticas integradas en una consola minimalista diseñada para la velocidad.
          </p>
        </div>

        <div class="grid grid-cols-3 gap-3 w-full">
          <div class="bg-white border border-slate-200/60 p-4 rounded-xl shadow-[0_2px_8px_rgba(0,0,0,0.02)] hover:border-slate-300 transition-all">
            <div class="h-1.5 w-1.5 rounded-full bg-emerald-500 mb-2"></div>
            <p class="text-sm font-bold text-slate-900">Catálogo</p>
            <p class="text-[11px] text-slate-400 mt-0.5">Indexado inteligente</p>
          </div>
          <div class="bg-white border border-slate-200/60 p-4 rounded-xl shadow-[0_2px_8px_rgba(0,0,0,0.02)] hover:border-slate-300 transition-all">
            <div class="h-1.5 w-1.5 rounded-full bg-blue-500 mb-2"></div>
            <p class="text-sm font-bold text-slate-900">Préstamos</p>
            <p class="text-[11px] text-slate-400 mt-0.5">Trazabilidad total</p>
          </div>
          <div class="bg-white border border-slate-200/60 p-4 rounded-xl shadow-[0_2px_8px_rgba(0,0,0,0.02)] hover:border-slate-300 transition-all">
            <div class="h-1.5 w-1.5 rounded-full bg-slate-400 mb-2"></div>
            <p class="text-sm font-bold text-slate-900">Acceso</p>
            <p class="text-[11px] text-slate-400 mt-0.5">Seguridad RBAC</p>
          </div>
        </div>

        <div class="hidden md:block rounded-xl bg-white border border-slate-200/70 p-5 shadow-[0_12px_30px_rgba(0,0,0,0.03)] relative overflow-hidden">
          <div class="flex items-center justify-between border-b border-slate-100 pb-3 mb-4">
            <div class="flex items-center gap-2">
              <BookOpen class="h-4 w-4 text-slate-800" />
              <span class="text-xs font-bold text-slate-800">Core Engine V3</span>
            </div>
            <span class="text-[10px] bg-emerald-50 text-emerald-700 font-bold px-2 py-0.5 rounded-md border border-emerald-100">Live</span>
          </div>
          <p class="text-sm text-slate-600 font-medium leading-relaxed">
            Consola central optimizada para una latencia cero. Diseñada específicamente para ofrecer una experiencia limpia al cliente final.
          </p>
        </div>
      </section>

      <form class="bg-white border border-slate-200/80 rounded-2xl p-6 sm:p-8 shadow-[0_20px_40px_rgba(0,0,0,0.04)] grid gap-5 w-full relative" @submit.prevent="submit">
        
        <div>
          <span class="text-[10px] font-bold uppercase tracking-widest text-slate-400">Autenticación</span>
          <h2 class="mt-0.5 text-xl font-bold text-slate-950 tracking-tight">Ingresar al sistema</h2>
        </div>

        <div class="grid grid-cols-2 gap-2 bg-slate-50 p-1 rounded-xl border border-slate-200/40">
          <button
            type="button"
            class="rounded-lg py-2.5 px-3 text-center text-xs font-bold transition-all duration-200 flex items-center justify-center gap-2"
            :class="selectedProfile === 'admin' 
              ? 'bg-white text-slate-950 shadow-sm border border-slate-200/50' 
              : 'text-slate-500 hover:text-slate-900'"
            @click="selectProfile('admin')"
          >
            <UsersRound class="h-3.5 w-3.5" />
            <span>Administrador</span>
          </button>

          <button
            type="button"
            class="rounded-lg py-2.5 px-3 text-center text-xs font-bold transition-all duration-200 flex items-center justify-center gap-2"
            :class="selectedProfile === 'user' 
              ? 'bg-white text-slate-950 shadow-sm border border-slate-200/50' 
              : 'text-slate-500 hover:text-slate-900'"
            @click="selectProfile('user')"
          >
            <UserRound class="h-3.5 w-3.5" />
            <span>Usuario</span>
          </button>
        </div>

        <div class="flex items-center my-1">
          <div class="flex-grow border-t border-slate-100"></div>
          <span class="mx-3 text-[9px] font-bold text-slate-300 uppercase tracking-widest">o credenciales</span>
          <div class="flex-grow border-t border-slate-100"></div>
        </div>

        <label class="grid gap-1.5">
          <span class="text-xs font-semibold text-slate-600">Identificador</span>
          <span class="relative block w-full">
            <UserRound class="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-slate-400" />
            <input 
              v-model="username" 
              class="w-full pl-9 pr-4 py-2.5 bg-white border border-slate-200 rounded-xl text-sm font-medium text-slate-900 placeholder-slate-400 focus:outline-none focus:border-slate-950 focus:ring-1 focus:ring-slate-950 transition-all" 
              autocomplete="username" 
              placeholder="nombre@dominio" 
              required 
            />
          </span>
        </label>

        <label class="grid gap-1.5">
          <span class="text-xs font-semibold text-slate-600">Contraseña</span>
          <span class="relative block w-full">
            <KeyRound class="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-slate-400" />
            <input 
              v-model="password" 
              class="w-full pl-9 pr-4 py-2.5 bg-white border border-slate-200 rounded-xl text-sm font-medium text-slate-900 placeholder-slate-400 focus:outline-none focus:border-slate-950 focus:ring-1 focus:ring-slate-950 transition-all" 
              type="password" 
              autocomplete="current-password" 
              placeholder="••••••••" 
              required 
            />
          </span>
        </label>

        <div v-if="error" class="rounded-xl border border-rose-100 bg-rose-50/50 p-3 flex items-start gap-2.5 shadow-sm animate-in fade-in duration-200">
          <LockKeyhole class="h-4 w-4 text-rose-600 shrink-0 mt-0.5" />
          <p class="text-xs font-medium text-rose-900 leading-normal">{{ error }}</p>
        </div>

        <button 
          class="h-11 w-full mt-2 rounded-xl bg-slate-950 text-white font-medium text-sm flex items-center justify-center gap-2 shadow-md shadow-slate-950/10 hover:bg-slate-900 active:scale-[0.98] transition-all duration-150 disabled:opacity-50 group" 
          :disabled="loading"
        >
          <span>{{ loading ? 'Conectando...' : 'Iniciar sesión' }}</span>
          <ArrowRight v-if="!loading" class="h-4 w-4 transition-transform duration-200 group-hover:translate-x-0.5 text-slate-300" />
        </button>

      </form>
    </div>
  </main>
</template>