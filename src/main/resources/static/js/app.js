// Student Result Portal - Client Logic
const API_BASE = '/api/results';

// DOM References
const searchForm        = document.getElementById('searchForm');
const rollNumberInput   = document.getElementById('rollNumberInput');
const resultContainer   = document.getElementById('resultContainer');
const notFoundCard      = document.getElementById('notFoundCard');
const notFoundMsg       = document.getElementById('notFoundMsg');
const toastEl           = document.getElementById('toast');

const studentTableBody  = document.getElementById('studentTableBody');
const tableSearchInput  = document.getElementById('tableSearchInput');
const resultModal       = document.getElementById('resultModal');
const resultForm        = document.getElementById('resultForm');
const btnOpenAddModal   = document.getElementById('btnOpenAddModal');
const btnCloseModal     = document.getElementById('btnCloseModal');
const btnCancelModal    = document.getElementById('btnCancelModal');
const modalTitle        = document.getElementById('modalTitle');

const formStudentId     = document.getElementById('formStudentId');
const formRollNumber    = document.getElementById('formRollNumber');
const formStudentName   = document.getElementById('formStudentName');
const formGradeClass    = document.getElementById('formGradeClass');
const formEmail         = document.getElementById('formEmail');
const formMath          = document.getElementById('formMath');
const formScience       = document.getElementById('formScience');
const formEnglish       = document.getElementById('formEnglish');
const formCS            = document.getElementById('formCS');
const formSocial        = document.getElementById('formSocial');

const liveTotal         = document.getElementById('liveTotal');
const livePercent       = document.getElementById('livePercent');
const liveGrade         = document.getElementById('liveGrade');
const liveStatus        = document.getElementById('liveStatus');

let cachedStudents = [];

// ─── Init ────────────────────────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
    initTabs();
    initSearch();
    initModal();
    initLiveCalc();
    loadStats();
    loadStudents();
});

// ─── Toast ───────────────────────────────────────────────────────────────────
function showToast(msg, duration = 3000) {
    if (!toastEl) return;
    toastEl.textContent = msg;
    toastEl.classList.remove('hidden');
    setTimeout(() => toastEl.classList.add('hidden'), duration);
}

// ─── Tabs ────────────────────────────────────────────────────────────────────
function initTabs() {
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
            document.querySelectorAll('.tab-pane').forEach(p => {
                p.classList.remove('active');
                p.classList.add('hidden');
            });
            btn.classList.add('active');
            const target = document.getElementById(btn.getAttribute('data-tab'));
            if (target) {
                target.classList.add('active');
                target.classList.remove('hidden');
            }
            if (btn.getAttribute('data-tab') === 'adminTab') {
                loadStats();
                loadStudents();
            }
        });
    });
}

// ─── Search ──────────────────────────────────────────────────────────────────
function initSearch() {
    searchForm.addEventListener('submit', e => {
        e.preventDefault();
        const roll = rollNumberInput.value.trim();
        if (roll) fetchResult(roll);
    });

    document.querySelectorAll('.link-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            const roll = btn.getAttribute('data-roll');
            if (roll) {
                rollNumberInput.value = roll;
                fetchResult(roll);
            }
        });
    });

    document.getElementById('btnPrintCard')?.addEventListener('click', () => window.print());

    document.getElementById('btnSearchAnother')?.addEventListener('click', () => {
        resultContainer.classList.add('hidden');
        rollNumberInput.value = '';
        rollNumberInput.focus();
    });
}

async function fetchResult(roll) {
    resultContainer.classList.add('hidden');
    notFoundCard.classList.add('hidden');
    try {
        const res  = await fetch(`${API_BASE}/${encodeURIComponent(roll)}`);
        const json = await res.json();
        if (res.ok && json.success && json.data) {
            renderResult(json.data);
            resultContainer.classList.remove('hidden');
        } else {
            notFoundMsg.textContent = json.message || `No record found for roll number: ${roll}`;
            notFoundCard.classList.remove('hidden');
        }
    } catch {
        showToast('Cannot connect to server. Make sure the backend is running.');
    }
}

function renderResult(s) {
    document.getElementById('resStudentName').textContent = s.studentName;
    document.getElementById('resRollNumber').textContent  = s.rollNumber;
    document.getElementById('resClass').textContent       = s.gradeClass;

    const badge = document.getElementById('resStatusBadge');
    badge.textContent = s.status;
    badge.className   = `badge ${s.status === 'PASS' ? 'badge-pass' : 'badge-fail'}`;

    const subjects = [
        { name: 'Mathematics',      marks: s.mathematics },
        { name: 'Science',          marks: s.science },
        { name: 'English',          marks: s.english },
        { name: 'Computer Science', marks: s.computerScience },
        { name: 'Social Studies',   marks: s.socialStudies }
    ];

    const tbody = document.getElementById('resMarksBody');
    tbody.innerHTML = '';
    subjects.forEach(sub => {
        const pass = sub.marks >= 35;
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${sub.name}</td>
            <td>100</td>
            <td>35</td>
            <td><strong>${sub.marks}</strong></td>
            <td><span class="pill ${pass ? 'pill-pass' : 'pill-fail'}">${pass ? 'Pass' : 'Fail'}</span></td>
        `;
        tbody.appendChild(tr);
    });

    document.getElementById('resTotalMarks').textContent = s.totalMarks;
    document.getElementById('resPercentage').textContent = `${s.percentage}%`;
    document.getElementById('resGrade').textContent      = s.grade;
    document.getElementById('resRemarks').textContent    = s.remarks || '-';
}

// ─── Stats ───────────────────────────────────────────────────────────────────
async function loadStats() {
    try {
        const res  = await fetch(`${API_BASE}/stats`);
        const json = await res.json();
        if (json.success && json.data) {
            const d = json.data;
            document.getElementById('statTotalStudents').textContent  = d.totalStudents;
            document.getElementById('statPassedStudents').textContent = d.passedStudents;
            document.getElementById('statFailedStudents').textContent = d.failedStudents;
            document.getElementById('statPassRate').textContent       = `${d.passPercentage}%`;
            document.getElementById('statHighestMarks').textContent   = `${d.highestMarks} / 500`;
        }
    } catch { /* stats are non-critical */ }
}

// ─── Student Table ────────────────────────────────────────────────────────────
async function loadStudents() {
    try {
        const res  = await fetch(API_BASE);
        const json = await res.json();
        if (json.success && json.data) {
            cachedStudents = json.data;
            renderTable(cachedStudents);
        }
    } catch { /* silent */ }
}

function renderTable(list) {
    if (!studentTableBody) return;
    studentTableBody.innerHTML = '';

    if (list.length === 0) {
        studentTableBody.innerHTML = `
            <tr><td colspan="8" style="text-align:center;padding:2rem;color:#6b7280;">
                No records found. Click "+ Add Result" to add one.
            </td></tr>`;
        return;
    }

    list.forEach(s => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td class="mono"><strong>${s.rollNumber}</strong></td>
            <td>${s.studentName}</td>
            <td>${s.gradeClass}</td>
            <td>${s.totalMarks} / 500</td>
            <td>${s.percentage}%</td>
            <td><strong>${s.grade}</strong></td>
            <td><span class="pill ${s.status === 'PASS' ? 'pill-pass' : 'pill-fail'}">${s.status}</span></td>
            <td>
                <div class="action-group">
                    <button class="icon-btn" title="View" onclick="viewCard('${s.rollNumber}')">👁</button>
                    <button class="icon-btn" title="Edit" onclick="editStudent(${s.id})">✏️</button>
                    <button class="icon-btn del" title="Delete" onclick="deleteStudent(${s.id}, '${s.rollNumber}')">🗑</button>
                </div>
            </td>
        `;
        studentTableBody.appendChild(tr);
    });
}

tableSearchInput?.addEventListener('input', e => {
    const q = e.target.value.toLowerCase();
    renderTable(cachedStudents.filter(s =>
        s.studentName.toLowerCase().includes(q) ||
        s.rollNumber.toLowerCase().includes(q)
    ));
});

// ─── Modal ───────────────────────────────────────────────────────────────────
function initModal() {
    btnOpenAddModal?.addEventListener('click', () => {
        modalTitle.textContent = 'Add New Student Result';
        resultForm.reset();
        formStudentId.value = '';
        formRollNumber.disabled = false;
        updateLive();
        resultModal.classList.remove('hidden');
    });

    const close = () => resultModal.classList.add('hidden');
    btnCloseModal?.addEventListener('click', close);
    btnCancelModal?.addEventListener('click', close);
    resultModal?.addEventListener('click', e => { if (e.target === resultModal) close(); });

    resultForm?.addEventListener('submit', async e => {
        e.preventDefault();
        const id = formStudentId.value;
        const payload = {
            rollNumber:      formRollNumber.value.trim().toUpperCase(),
            studentName:     formStudentName.value.trim(),
            gradeClass:      formGradeClass.value.trim(),
            email:           formEmail.value.trim() || null,
            mathematics:     parseInt(formMath.value)     || 0,
            science:         parseInt(formScience.value)  || 0,
            english:         parseInt(formEnglish.value)  || 0,
            computerScience: parseInt(formCS.value)       || 0,
            socialStudies:   parseInt(formSocial.value)   || 0
        };

        try {
            const res  = await fetch(id ? `${API_BASE}/${id}` : API_BASE, {
                method:  id ? 'PUT' : 'POST',
                headers: { 'Content-Type': 'application/json' },
                body:    JSON.stringify(payload)
            });
            const json = await res.json();
            if (res.ok && json.success) {
                showToast(id ? 'Result updated!' : 'Result saved!');
                close();
                loadStats();
                loadStudents();
            } else {
                showToast(json.message || 'Something went wrong.');
            }
        } catch {
            showToast('Failed to connect to server.');
        }
    });
}

window.editStudent = function(id) {
    const s = cachedStudents.find(s => s.id === id);
    if (!s) return;
    modalTitle.textContent      = `Edit: ${s.rollNumber}`;
    formStudentId.value         = s.id;
    formRollNumber.value        = s.rollNumber;
    formRollNumber.disabled     = false;
    formStudentName.value       = s.studentName;
    formGradeClass.value        = s.gradeClass;
    formEmail.value             = s.email || '';
    formMath.value              = s.mathematics;
    formScience.value           = s.science;
    formEnglish.value           = s.english;
    formCS.value                = s.computerScience;
    formSocial.value            = s.socialStudies;
    updateLive();
    resultModal.classList.remove('hidden');
};

window.deleteStudent = async function(id, roll) {
    if (!confirm(`Delete result for ${roll}?`)) return;
    try {
        const res  = await fetch(`${API_BASE}/${id}`, { method: 'DELETE' });
        const json = await res.json();
        if (res.ok && json.success) {
            showToast(`${roll} deleted.`);
            loadStats();
            loadStudents();
        } else {
            showToast(json.message || 'Delete failed.');
        }
    } catch {
        showToast('Error deleting record.');
    }
};

window.viewCard = function(roll) {
    document.querySelector('[data-tab="searchTab"]').click();
    rollNumberInput.value = roll;
    fetchResult(roll);
};

// ─── Live Calculation ────────────────────────────────────────────────────────
function initLiveCalc() {
    [formMath, formScience, formEnglish, formCS, formSocial].forEach(el => {
        el?.addEventListener('input', updateLive);
    });
}

function updateLive() {
    const m  = clamp(parseInt(formMath.value)    || 0);
    const s  = clamp(parseInt(formScience.value) || 0);
    const e  = clamp(parseInt(formEnglish.value) || 0);
    const cs = clamp(parseInt(formCS.value)      || 0);
    const ss = clamp(parseInt(formSocial.value)  || 0);

    const total   = m + s + e + cs + ss;
    const pct     = Math.round((total / 5.0) * 100) / 100;
    const passed  = m >= 35 && s >= 35 && e >= 35 && cs >= 35 && ss >= 35 && pct >= 40;

    let grade = 'F';
    if (passed) {
        if (pct >= 90) grade = 'A+';
        else if (pct >= 80) grade = 'A';
        else if (pct >= 70) grade = 'B+';
        else if (pct >= 60) grade = 'B';
        else if (pct >= 50) grade = 'C';
        else grade = 'D';
    }

    if (liveTotal)   liveTotal.textContent   = `${total} / 500`;
    if (livePercent) livePercent.textContent = `${pct}%`;
    if (liveGrade)   liveGrade.textContent   = grade;
    if (liveStatus)  liveStatus.textContent  = passed ? 'PASS' : 'FAIL';
}

function clamp(v) { return Math.min(100, Math.max(0, v)); }
