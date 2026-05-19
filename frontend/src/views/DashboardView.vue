<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import {
  BookOpenCheck,
  ChevronLeft,
  ChevronRight,
  Download,
  ImagePlus,
  LibraryBig,
  LogOut,
  Pencil,
  Plus,
  RefreshCw,
  Search,
  Trash2,
  UserPlus,
  UsersRound,
  X
} from '@lucide/vue'
import { useAuthStore } from '../stores/auth'
import type { Book, BookPayload, User, UserPayload } from '../types'
import { assignBook, createBook, deleteBook, exportBooksCsv, getBooks, updateBook } from '../services/bookService'
import { createUser, deleteUser, getUsers, updateUser } from '../services/userService'

const auth = useAuthStore()
const books = ref<Book[]>([])
const users = ref<User[]>([])
const activeTab = ref<'books' | 'users'>('books')
const loading = ref(false)
const message = ref('')
const error = ref('')
const search = ref('')
const currentBookPage = ref(1)
const showBookModal = ref(false)
const showUserModal = ref(false)
const editingBookId = ref<number | null>(null)
const editingUserId = ref<number | null>(null)
const thumbnailPreview = ref<string | null>(null)

const bookForm = reactive<BookPayload>({ title: '', author: '', year: new Date().getFullYear(), thumbnail: null })
const userForm = reactive<UserPayload>({ username: '', password: '', role: 'USER' })
const BOOKS_PER_PAGE = 6

const visibleUsers = computed(() => auth.isAdmin ? users.value : users.value.filter((user) => user.id === auth.user?.id))
const assignedCount = computed(() => books.value.filter((book) => book.assignedTo).length)
const filteredBooks = computed(() => {
  const term = search.value.trim().toLowerCase()
  if (!term) return books.value
  return books.value.filter((book) => [book.title, book.author, book.year.toString(), book.assignedTo?.username ?? 'sin asignar']
    .some((value) => value.toLowerCase().includes(term)))
})
const totalBookPages = computed(() => Math.max(1, Math.ceil(filteredBooks.value.length / BOOKS_PER_PAGE)))
const paginatedBooks = computed(() => {
  const start = (currentBookPage.value - 1) * BOOKS_PER_PAGE
  return filteredBooks.value.slice(start, start + BOOKS_PER_PAGE)
})
const bookPageStart = computed(() => filteredBooks.value.length === 0 ? 0 : (currentBookPage.value - 1) * BOOKS_PER_PAGE + 1)
const bookPageEnd = computed(() => Math.min(currentBookPage.value * BOOKS_PER_PAGE, filteredBooks.value.length))

onMounted(loadData)

watch(search, () => {
  currentBookPage.value = 1
})

watch(totalBookPages, (pages) => {
  if (currentBookPage.value > pages) {
    currentBookPage.value = pages
  }
})

async function loadData() {
  loading.value = true
  clearStatus()
  try {
    books.value = await getBooks()
    users.value = auth.isAdmin
      ? await getUsers()
      : auth.user ? [{ id: auth.user.id, username: auth.user.username, role: auth.user.role }] : []
  } catch {
    error.value = 'No se pudieron cargar los datos. Comprueba que el backend este iniciado.'
  } finally {
    loading.value = false
  }
}

async function saveBook() {
  clearStatus()
  try {
    if (editingBookId.value) {
      await updateBook(editingBookId.value, bookForm)
      message.value = 'Libro actualizado correctamente.'
    } else {
      await createBook(bookForm)
      message.value = 'Libro creado con miniatura.'
    }
    closeBookModal()
    await loadData()
  } catch {
    error.value = 'No se pudo guardar el libro. Revisa permisos, campos e imagen.'
  }
}

async function saveUser() {
  clearStatus()
  try {
    if (editingUserId.value) {
      await updateUser(editingUserId.value, userForm)
      message.value = 'Usuario actualizado.'
    } else {
      await createUser(userForm)
      message.value = 'Usuario creado.'
    }
    closeUserModal()
    await loadData()
  } catch {
    error.value = 'No se pudo guardar el usuario. Revisa permisos, duplicados o contrasena.'
  }
}

async function removeBook(id: number) {
  clearStatus()
  try {
    await deleteBook(id)
    message.value = 'Libro eliminado.'
    await loadData()
  } catch {
    error.value = 'No se pudo eliminar el libro.'
  }
}

async function removeUser(id: number) {
  clearStatus()
  try {
    await deleteUser(id)
    message.value = 'Usuario eliminado.'
    await loadData()
  } catch {
    error.value = 'No se pudo eliminar el usuario.'
  }
}

async function updateAssignment(book: Book, rawUserId: string) {
  clearStatus()
  const userId = rawUserId ? Number(rawUserId) : null
  try {
    if (!auth.isAdmin && userId !== auth.user?.id && userId !== null) {
      error.value = 'Solo puedes asignarte libros a ti.'
      return
    }
    await assignBook(book.id, userId)
    message.value = 'Asignacion actualizada.'
    await loadData()
  } catch {
    error.value = 'No se pudo actualizar la asignacion.'
  }
}

async function downloadCsv() {
  clearStatus()
  try {
    await exportBooksCsv()
  } catch {
    error.value = 'No se pudo exportar el CSV. Esta accion requiere rol ADMIN.'
  }
}

function goToBookPage(page: number) {
  currentBookPage.value = Math.min(Math.max(page, 1), totalBookPages.value)
}

function openNewBook() {
  resetBookForm()
  showBookModal.value = true
}

function editBook(book: Book) {
  editingBookId.value = book.id
  bookForm.title = book.title
  bookForm.author = book.author
  bookForm.year = book.year
  bookForm.thumbnail = null
  thumbnailPreview.value = book.thumbnail
  showBookModal.value = true
}

function openNewUser() {
  resetUserForm()
  showUserModal.value = true
}

function editUser(user: User) {
  editingUserId.value = user.id
  userForm.username = user.username
  userForm.password = ''
  userForm.role = user.role
  showUserModal.value = true
}

function onThumbnailChange(event: Event) {
  const file = (event.target as HTMLInputElement).files?.[0] ?? null
  bookForm.thumbnail = file
  thumbnailPreview.value = file ? URL.createObjectURL(file) : thumbnailPreview.value
}

function closeBookModal() {
  showBookModal.value = false
  resetBookForm()
}

function closeUserModal() {
  showUserModal.value = false
  resetUserForm()
}

function resetBookForm() {
  editingBookId.value = null
  bookForm.title = ''
  bookForm.author = ''
  bookForm.year = new Date().getFullYear()
  bookForm.thumbnail = null
  thumbnailPreview.value = null
}

function resetUserForm() {
  editingUserId.value = null
  userForm.username = ''
  userForm.password = ''
  userForm.role = 'USER'
}

function clearStatus() {
  message.value = ''
  error.value = ''
}

function canEditBook(book: Book) {
  return auth.isAdmin || book.assignedTo?.id === auth.user?.id
}

function logout() {
  auth.logout()
  location.href = '/login'
}
</script>

<template>
  <main class="min-h-screen bg-[#fafafa] text-slate-950 flex flex-col relative font-sans antialiased selection:bg-slate-950 selection:text-white">
    
    <header class="w-full border-b border-slate-200/60 bg-white/80 backdrop-blur sticky top-0 z-30 shadow-[0_2px_12px_rgba(0,0,0,0.02)]">
      <div class="mx-auto flex flex-col sm:flex-row min-h-[76px] items-center justify-between gap-4 px-6 py-3 max-w-7xl">
        
        <div class="flex items-center gap-3">
          <span class="flex h-10 w-10 items-center justify-center rounded-xl bg-slate-950 font-black text-white shadow-[0_4px_12px_rgba(0,0,0,0.08)]">B</span>
          <div>
            <p class="text-base font-bold tracking-tight leading-tight text-slate-950">Bookify</p>
            <p class="text-[10px] font-semibold uppercase text-slate-400 tracking-wider">Gestión bibliotecaria</p>
          </div>
        </div>

        <div class="flex flex-wrap items-center justify-center sm:justify-end gap-2.5 w-full sm:w-auto">
          <span class="inline-flex items-center gap-1.5 border border-slate-200 bg-slate-50 text-slate-700 px-3 py-1.5 text-xs font-bold rounded-xl shadow-inner">
            <span class="h-2 w-2 rounded-full bg-emerald-500 animate-pulse"></span>
            {{ auth.user?.username }} • {{ auth.user?.role }}
          </span>
          <button class="inline-flex items-center gap-1.5 bg-white border border-slate-200 hover:border-slate-300 text-slate-700 px-3 py-1.5 text-xs font-bold rounded-xl shadow-sm transition-all active:scale-[0.98]" @click="loadData">
            <RefreshCw class="h-3.5 w-3.5 text-slate-500" />Recargar
          </button>
          <button v-if="auth.isAdmin" class="inline-flex items-center gap-1.5 bg-white border border-slate-200 hover:border-slate-300 text-slate-700 px-3 py-1.5 text-xs font-bold rounded-xl shadow-sm transition-all active:scale-[0.98]" @click="downloadCsv">
            <Download class="h-3.5 w-3.5 text-slate-500" />CSV
          </button>
          <button class="inline-flex items-center gap-1.5 bg-slate-950 hover:bg-slate-900 text-white px-3 py-1.5 text-xs font-bold rounded-xl shadow-md shadow-slate-950/5 transition-all active:scale-[0.98]" @click="logout">
            <LogOut class="h-3.5 w-3.5 text-slate-300" />Salir
          </button>
        </div>
      </div>
    </header>

    <div class="mx-auto flex flex-col lg:grid lg:grid-rows-[auto_auto_1fr] lg:h-[calc(100vh-76px)] w-full max-w-7xl gap-5 p-6 overflow-hidden flex-1">
      
      <section class="grid gap-4 sm:grid-cols-2 lg:grid-cols-[1.4fr_.8fr_.8fr]">
        <div class="rounded-2xl bg-gradient-to-br from-slate-950 via-slate-900 to-slate-800 p-6 text-white shadow-lg shadow-slate-950/5 flex flex-col justify-center relative overflow-hidden group">
          <div class="absolute -right-10 -bottom-10 w-40 h-40 bg-emerald-500/[0.08] rounded-full blur-2xl pointer-events-none"></div>
          <p class="text-[10px] font-bold uppercase tracking-widest text-emerald-400">Consola Central</p>
          <h1 class="mt-1 text-xl sm:text-2xl font-bold tracking-tight leading-snug">Infraestructura simplificada de control.</h1>
          <p class="mt-2 text-xs text-slate-300 font-medium leading-relaxed max-w-md">Supervisión en tiempo real de registros bibliográficos, flujos de asignación y perfiles operativos con rendimiento optimizado.</p>
        </div>
        
        <div class="flex flex-col justify-between p-5 bg-white border border-slate-200/70 rounded-2xl shadow-[0_4px_12px_rgba(0,0,0,0.01)] hover:border-slate-300 transition-all">
          <div class="flex items-center justify-between">
            <p class="text-xs font-bold text-slate-400 uppercase tracking-wider">Libros Indexados</p>
            <div class="p-2 bg-slate-50 rounded-xl border border-slate-100"><LibraryBig class="h-4 w-4 text-slate-700" /></div>
          </div>
          <div class="mt-4">
            <p class="text-4xl font-black text-slate-950 tracking-tight leading-none">{{ books.length }}</p>
            <p class="text-[11px] font-medium text-emerald-600 mt-1">Catálogo activo</p>
          </div>
        </div>

        <div class="flex flex-col justify-between p-5 bg-white border border-slate-200/70 rounded-2xl shadow-[0_4px_12px_rgba(0,0,0,0.01)] hover:border-slate-300 transition-all">
          <div class="flex items-center justify-between">
            <p class="text-xs font-bold text-slate-400 uppercase tracking-wider">{{ auth.isAdmin ? 'Usuarios Activos' : 'Asignaciones' }}</p>
            <div class="p-2 bg-slate-50 rounded-xl border border-slate-100"><UsersRound class="h-4 w-4 text-slate-700" /></div>
          </div>
          <div class="mt-4">
            <p class="text-4xl font-black text-slate-950 tracking-tight leading-none">{{ auth.isAdmin ? users.length : assignedCount }}</p>
            <p class="text-[11px] font-medium text-blue-600 mt-1">Filtro por rol activo</p>
          </div>
        </div>
      </section>

      <div class="flex flex-col md:flex-row md:items-center justify-between gap-4 bg-white p-3 rounded-2xl border border-slate-200/70 shadow-[0_4px_12px_rgba(0,0,0,0.01)]">
        <div class="grid grid-cols-2 sm:flex gap-1.5 bg-slate-50 p-1 rounded-xl border border-slate-200/40">
          <button class="px-4 py-2 text-xs font-bold rounded-lg transition-all" :class="activeTab === 'books' ? 'bg-white text-slate-950 shadow-sm border border-slate-200/50' : 'text-slate-500 hover:text-slate-900'" @click="activeTab = 'books'">Módulo Libros</button>
          <button v-if="auth.isAdmin" class="px-4 py-2 text-xs font-bold rounded-lg transition-all" :class="activeTab === 'users' ? 'bg-white text-slate-950 shadow-sm border border-slate-200/50' : 'text-slate-500 hover:text-slate-900'" @click="activeTab = 'users'">Módulo Usuarios</button>
        </div>

        <div class="flex flex-col sm:flex-row gap-2 items-stretch sm:items-center w-full md:w-auto">
          <label v-if="activeTab === 'books'" class="relative w-full sm:w-72 lg:w-80 block">
            <Search class="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-slate-400" />
            <input v-model="search" class="w-full pl-9 pr-4 py-2 bg-white border border-slate-200 rounded-xl text-xs font-medium text-slate-900 placeholder-slate-400 focus:outline-none focus:border-slate-950 focus:ring-1 focus:ring-slate-950 transition-all" placeholder="Buscar por título, autor o estado..." />
          </label>
          <button v-if="activeTab === 'books' && auth.isAdmin" class="inline-flex items-center justify-center gap-1.5 bg-slate-950 hover:bg-slate-900 text-white text-xs font-bold py-2 px-4 rounded-xl shadow-md shadow-slate-950/5 transition-all active:scale-[0.98]" @click="openNewBook">
            <Plus class="h-3.5 w-3.5" />Nuevo Libro
          </button>
          <button v-if="activeTab === 'users' && auth.isAdmin" class="inline-flex items-center justify-center gap-1.5 bg-slate-950 hover:bg-slate-900 text-white text-xs font-bold py-2 px-4 rounded-xl shadow-md shadow-slate-950/5 transition-all active:scale-[0.98]" @click="openNewUser">
            <UserPlus class="h-3.5 w-3.5" />Nuevo Usuario
          </button>
        </div>
      </div>

      <p v-if="message" class="fixed bottom-20 right-6 z-50 rounded-xl border border-emerald-100 bg-emerald-50 px-4 py-3 text-xs font-bold text-emerald-800 shadow-xl shadow-emerald-950/5 animate-in fade-in slide-in-from-bottom-2 duration-150">{{ message }}</p>
      <p v-if="error" class="fixed bottom-20 right-6 z-50 rounded-xl border border-rose-100 bg-rose-50 px-4 py-3 text-xs font-bold text-rose-800 shadow-xl shadow-rose-950/5 animate-in fade-in slide-in-from-bottom-2 duration-150">{{ error }}</p>
      <p v-if="loading" class="fixed bottom-20 right-6 z-50 rounded-xl border border-slate-200 bg-white px-4 py-3 text-xs font-bold text-slate-500 shadow-xl shadow-slate-950/5">Procesando consulta...</p>

      <section v-if="activeTab === 'books'" class="grid gap-4 sm:grid-cols-2 xl:grid-cols-3 lg:overflow-y-auto pr-1 h-full content-start pb-28 lg:pb-6">
        <article v-for="book in paginatedBooks" :key="book.id" class="bg-white border border-slate-200/70 rounded-xl shadow-[0_2px_8px_rgba(0,0,0,0.01)] hover:border-slate-300 hover:shadow-[0_4px_16px_rgba(0,0,0,0.03)] transition-all duration-200 flex flex-col justify-between overflow-hidden">
          <div class="grid grid-cols-[100px_1fr] gap-4 p-4">
            <div class="h-32 w-full shrink-0 relative rounded-lg overflow-hidden bg-slate-50 border border-slate-100 flex items-center justify-center">
              <img v-if="book.thumbnail" :src="book.thumbnail" :alt="book.title" class="h-full w-full object-cover transition-transform duration-300 hover:scale-105" />
              <div v-else class="text-slate-400 flex flex-col items-center justify-center">
                <LibraryBig class="h-6 w-6 text-slate-300" />
              </div>
            </div>
            <div class="flex flex-col justify-between min-w-0">
              <div>
                <span class="inline-block text-[10px] font-bold px-2 py-0.5 rounded-md border" :class="book.assignedTo ? 'border-amber-100 bg-amber-50 text-amber-700' : 'border-emerald-100 bg-emerald-50 text-emerald-700'">
                  {{ book.assignedTo ? 'Prestado' : 'Disponible' }}
                </span>
                <h2 class="mt-1.5 line-clamp-2 text-sm font-bold text-slate-900 leading-snug" :title="book.title">{{ book.title }}</h2>
                <p class="mt-0.5 truncate text-[11px] font-medium text-slate-400">{{ book.author }} • {{ book.year }}</p>
              </div>
            </div>
          </div>
          
          <div class="px-4 pb-4 pt-0 border-t border-slate-50 bg-[#fafafa]/50 mt-auto">
            <select class="w-full mt-3 text-xs font-semibold py-1.5 px-2 bg-white border border-slate-200 rounded-lg focus:outline-none focus:border-slate-950 transition-all text-slate-700" :value="book.assignedTo?.id ?? ''" @change="updateAssignment(book, ($event.target as HTMLSelectElement).value)">
              <option value="">Sin asignar (Almacén)</option>
              <option v-for="user in visibleUsers" :key="user.id" :value="user.id">Asignar a: {{ user.username }}</option>
            </select>
            <div class="mt-2 flex gap-2">
              <button class="inline-flex items-center justify-center gap-1 bg-white border border-slate-200 hover:border-slate-300 disabled:opacity-40 text-slate-700 h-8 flex-1 text-xs font-bold rounded-lg transition-all" :disabled="!canEditBook(book)" @click="editBook(book)">
                <Pencil class="h-3 w-3" />Editar
              </button>
              <button v-if="auth.isAdmin" class="inline-flex items-center justify-center bg-white border border-rose-100 hover:border-rose-200 text-rose-600 h-8 px-2.5 rounded-lg transition-all hover:bg-rose-50/50" @click="removeBook(book.id)" title="Eliminar obra">
                <Trash2 class="h-3.5 w-3.5" />
              </button>
            </div>
          </div>
        </article>
      </section>

      <section v-if="activeTab === 'users' && auth.isAdmin" class="grid gap-4 sm:grid-cols-2 xl:grid-cols-3 lg:overflow-y-auto pr-1 h-full content-start pb-28 lg:pb-6">
        <article v-for="user in users" :key="user.id" class="bg-white border border-slate-200/70 rounded-xl p-4 shadow-[0_2px_8px_rgba(0,0,0,0.01)] flex flex-col justify-between">
          <div>
            <div class="flex items-start justify-between gap-3">
              <div class="flex items-center gap-2.5">
                <span class="flex h-9 w-9 items-center justify-center rounded-lg bg-slate-100 text-xs font-black text-slate-800 border border-slate-200/50">{{ user.username.slice(0, 2).toUpperCase() }}</span>
                <div class="min-w-0">
                  <h2 class="text-sm font-bold text-slate-900 truncate max-w-[140px]">{{ user.username }}</h2>
                  <span class="inline-block text-[9px] font-bold px-1.5 py-0.2 rounded border uppercase tracking-wider mt-0.5" :class="user.role === 'ADMIN' ? 'border-slate-900 bg-slate-950 text-white' : 'border-slate-200 bg-slate-50 text-slate-600'">{{ user.role }}</span>
                </div>
              </div>
              <BookOpenCheck class="h-4 w-4 text-slate-400 shrink-0" />
            </div>
            <div class="mt-4 rounded-xl bg-slate-50 border border-slate-200/30 p-2 text-[11px] font-medium text-slate-500">
              Posee <span class="font-bold text-slate-950">{{ books.filter((book) => book.assignedTo?.id === user.id).length }}</span> obras asignadas
            </div>
          </div>
          <div class="mt-4 flex gap-2 border-t border-slate-50 pt-3">
            <button class="inline-flex items-center justify-center gap-1 bg-white border border-slate-200 hover:border-slate-300 text-slate-700 h-8 flex-1 text-xs font-bold rounded-lg transition-all" @click="editUser(user)">
              <Pencil class="h-3 w-3" />Ajustes
            </button>
            <button class="inline-flex items-center justify-center bg-white border border-rose-100 hover:border-rose-200 text-rose-600 h-8 px-2.5 rounded-lg transition-all hover:bg-rose-50/50" @click="removeUser(user.id)">
              <Trash2 class="h-3.5 w-3.5" />
            </button>
          </div>
        </article>
      </section>

    </div>

    <footer class="sticky bottom-0 left-0 w-full bg-white/95 backdrop-blur border-t border-slate-200/80 shadow-[0_-4px_12px_rgba(0,0,0,0.02)] z-20">
      <div class="mx-auto max-w-7xl flex flex-col sm:flex-row gap-3 items-center justify-between px-6 py-3.5">
        <p v-if="activeTab === 'books'" class="text-xs font-medium text-slate-500 order-2 sm:order-1">
          Mostrando <span class="font-bold text-slate-950">{{ bookPageStart }} - {{ bookPageEnd }}</span> de <span class="font-bold text-slate-950">{{ filteredBooks.length }}</span> registros
        </p>
        <p v-else class="text-xs font-medium text-slate-500 order-2 sm:order-1">
          <span class="font-bold text-slate-950">{{ users.length }}</span> cuentas registradas en base de datos
        </p>
        
        <div v-if="activeTab === 'books'" class="flex items-center gap-1.5 order-1 sm:order-2 w-full sm:w-auto justify-center">
          <button class="w-8 h-8 inline-flex items-center justify-center bg-white border border-slate-200 hover:border-slate-300 rounded-lg text-slate-600 shadow-sm disabled:opacity-40 disabled:pointer-events-none transition-all active:scale-95" :disabled="currentBookPage === 1" @click="goToBookPage(currentBookPage - 1)">
            <ChevronLeft class="h-4 w-4" />
          </button>
          <span class="rounded-lg border border-slate-200/60 bg-slate-50/50 px-3 py-1.5 text-xs font-bold text-slate-800 min-w-[64px] text-center shadow-inner">
            {{ currentBookPage }} / {{ totalBookPages }}
          </span>
          <button class="w-8 h-8 inline-flex items-center justify-center bg-white border border-slate-200 hover:border-slate-300 rounded-lg text-slate-600 shadow-sm disabled:opacity-40 disabled:pointer-events-none transition-all active:scale-95" :disabled="currentBookPage === totalBookPages" @click="goToBookPage(currentBookPage + 1)">
            <ChevronRight class="h-4 w-4" />
          </button>
        </div>
      </div>
    </footer>

    <div v-if="showBookModal" class="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/40 p-4 backdrop-blur-xs overflow-y-auto">
      <form class="bg-white border border-slate-200/80 grid w-full max-w-md gap-4 p-6 rounded-2xl shadow-2xl max-h-[90vh] overflow-y-auto my-auto relative animate-in fade-in zoom-in-95 duration-150" @submit.prevent="saveBook">
        <div class="flex items-center justify-between sticky top-0 bg-white pb-2 border-b border-slate-100 z-10">
          <div>
            <span class="text-[9px] font-bold uppercase text-emerald-600 tracking-wider">{{ editingBookId ? 'Modificar Entrada' : 'Indexación' }}</span>
            <h2 class="text-base font-bold text-slate-950">{{ editingBookId ? 'Editar metadatos del libro' : 'Registrar nueva obra' }}</h2>
          </div>
          <button type="button" class="text-slate-400 hover:bg-slate-100 p-1.5 rounded-lg transition-all" @click="closeBookModal"><X class="h-4 w-4" /></button>
        </div>
        <label class="grid gap-1">
          <span class="text-xs font-semibold text-slate-600">Título de la Obra</span>
          <input v-model="bookForm.title" class="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs font-medium text-slate-900 focus:outline-none focus:border-slate-950 transition-all" placeholder="Ej. El Quijote" required />
        </label>
        <label class="grid gap-1">
          <span class="text-xs font-semibold text-slate-600">Autor / Escritor principal</span>
          <input v-model="bookForm.author" class="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs font-medium text-slate-900 focus:outline-none focus:border-slate-950 transition-all" placeholder="Ej. Miguel de Cervantes" required />
        </label>
        <label class="grid gap-1">
          <span class="text-xs font-semibold text-slate-600">Año de Edición</span>
          <input v-model.number="bookForm.year" class="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs font-medium text-slate-900 focus:outline-none focus:border-slate-950 transition-all" type="number" min="1" required />
        </label>
        <label class="grid gap-1.5">
          <span class="text-xs font-semibold text-slate-600">Miniatura / Portada</span>
          <div class="grid gap-3 rounded-xl border border-dashed border-slate-200 bg-slate-50/50 p-3 text-center">
            <img v-if="thumbnailPreview" :src="thumbnailPreview" alt="Vista previa" class="h-32 w-full rounded-lg object-contain mx-auto bg-white p-1 border border-slate-100 shadow-sm" />
            <div v-else class="flex flex-col items-center justify-center h-24 text-slate-400"><ImagePlus class="h-5 w-5 text-slate-300 mb-1" /><span class="text-[10px] font-medium">Soporta formatos PNG, JPG</span></div>
            <input class="w-full text-[11px] font-semibold text-slate-600 bg-white border border-slate-200 rounded-lg p-1" type="file" accept="image/*" @change="onThumbnailChange" />
          </div>
        </label>
        <button class="h-10 w-full bg-slate-950 hover:bg-slate-900 text-white text-xs font-bold rounded-xl shadow-md transition-all mt-2 active:scale-98">{{ editingBookId ? 'Guardar Cambios' : 'Indexar Libro' }}</button>
      </form>
    </div>

    <div v-if="showUserModal" class="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/40 p-4 backdrop-blur-xs overflow-y-auto">
      <form class="bg-white border border-slate-200/80 grid w-full max-w-sm gap-4 p-6 rounded-2xl shadow-2xl my-auto animate-in fade-in zoom-in-95 duration-150" @submit.prevent="saveUser">
        <div class="flex items-center justify-between pb-2 border-b border-slate-100">
          <div>
            <span class="text-[9px] font-bold uppercase text-emerald-600 tracking-wider">{{ editingUserId ? 'Sistemas' : 'Credenciales' }}</span>
            <h2 class="text-base font-bold text-slate-950">{{ editingUserId ? 'Modificar perfil de usuario' : 'Dar de alta usuario' }}</h2>
          </div>
          <button type="button" class="text-slate-400 hover:bg-slate-100 p-1.5 rounded-lg transition-all" @click="closeUserModal"><X class="h-4 w-4" /></button>
        </div>
        <label class="grid gap-1">
          <span class="text-xs font-semibold text-slate-600">Nombre de Usuario</span>
          <input v-model="userForm.username" class="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs font-medium text-slate-900 focus:outline-none focus:border-slate-950 transition-all" placeholder="Ej. bibliotecario_pro" required />
        </label>
        <label class="grid gap-1">
          <span class="text-xs font-semibold text-slate-600">Clave secreta / Contraseña</span>
          <input v-model="userForm.password" class="w-full px-3 py-2 border border-slate-200 rounded-xl text-xs font-medium text-slate-900 focus:outline-none focus:border-slate-950 transition-all" type="password" :placeholder="editingUserId ? 'Dejar en blanco si no cambia' : '••••••••'" :required="!editingUserId" />
        </label>
        <label class="grid gap-1">
          <span class="text-xs font-semibold text-slate-600">Rango de Acceso (Rol)</span>
          <select v-model="userForm.role" class="w-full px-3 py-2 bg-white border border-slate-200 rounded-xl text-xs font-semibold text-slate-700 focus:outline-none focus:border-slate-950 transition-all">
            <option value="USER">USER (Lectura e interacción estándar)</option>
            <option value="ADMIN">ADMIN (Acceso total de infraestructura)</option>
          </select>
        </label>
        <button class="h-10 w-full bg-slate-950 hover:bg-slate-900 text-white text-xs font-bold rounded-xl shadow-md transition-all mt-2 active:scale-98">{{ editingUserId ? 'Actualizar Cuenta' : 'Crear Cuenta' }}</button>
      </form>
    </div>
  </main>
</template>