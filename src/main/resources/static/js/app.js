// Student Result Portal - Client Logic
const API_BASE = '/api/results';

// DOM Elements
const searchForm = document.getElementById('searchForm');
const rollNumberInput = document.getElementById('rollNumberInput');
const resultContainer = document.getElementById('resultContainer');
const notFoundCard = document.getElementById('notFoundCard');
const notFoundMsg = document.getElementById('notFoundMsg');
const toastEl = document.getElementById('toast');

// Navigation Tabs
const navTabs = document.querySelectorAll('.nav-tab');
const tabPanes = document.querySelectorAll('.tab-pane');

// Admin Elements
const studentTableBody = document.getElementById('studentTableBody');
const tableSearchInput = document.getElementById('tableSearchInput');
const resultModal = document.getElementById('resultModal');
const resultForm = document.getElementById('resultForm');
const btnOpenAddModal = document.getElementById('btnOpenAddModal');
const btnCloseModal = document.getElementById('btnCloseModal');
const btnCancelModal = document.getElementById('btnCancelModal');
const modalTitle = document.getElementById('modalTitle');

// Form inputs
const formStudentId = document.getElementById('formStudentId');
const formRollNumber = document.getElementById('formRollNumber');
const formStudentName = document.getElementById('formStudentName');
const formGradeClass = document.getElementById('formGradeClass');
const formEmail = document.getElementById('formEmail');
const formMath = document.getElementById('formMath');
const formScience = document.getElementById('formScience');
const formEnglish = document.getElementById('formEnglish');
const formCS = document.getElementById('formCS');
const formSocial = document.getElementById('formSocial');

// Live calculation elements
const liveTotal = document.getElementById('liveTotal');
const livePercent = document.getElementById('livePercent');
const liveGrade = document.getElementById('liveGrade');
const liveStatus = document.getElementById('liveStatus');

// Stats elements
const statTotalStudents = document.getElementById('statTotalStudents');
const statPassedStudents = document.getElementById('statPassedStudents');
const statFailedStudents = document.getElementById('statFailedStudents');
const statPassRate = document.getElementById('statPassRate');
const statHighestMarks = document.getElementById('statHighestMarks');

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    initTabs();
    initSearch();
    initAdminModal();
    initLiveCalculation();
    loadDashboardStats();
    loadAllStudents();
});

// Toast notification helper
function showToast(message, duration = 3000) {
    if (!toastEl) return;
    toastEl.textContent = message;
    toastEl.classList.remove('hidden');
    setTimeout(() => {
        toastEl.classList.add('hidden');
    }, duration);
}

// Tab navigation handler
function initTabs() {
    navTabs.forEach(tab => {
        tab.addEventListener('click', () => {
            navTabs.forEach(t => t.classList.remove('active'));
            tabPanes.forEach(p => p.classList.remove('active'));

            tab.classList.add('active');
            const targetPane = document.getElementById(tab.getAttribute('data-tab'));
            if (targetPane) targetPane.classList.add('active');

            if (tab.getAttribute('data-tab') === 'adminTab') {
                loadDashboardStats();
                loadAllStudents();
            }
        });
    });
}

// Student Search Flow
function initSearch() {
    if (searchForm) {
        searchForm.addEventListener('submit', (e) => {
            e.preventDefault();
            const roll = rollNumberInput.value.trim();
            if (roll) fetchStudentResult(roll);
        });
    }

    // Quick chips
    document.querySelectorAll('.chip').forEach(chip => {
        chip.addEventListener('click', () => {
            const roll = chip.getAttribute('data-roll');
            if (roll) {
                rollNumberInput.value = roll;
                fetchStudentResult(roll);
            }
        });
    });

    const btnPrint = document.getElementById('btnPrintCard');
    if (btnPrint) {
        btnPrint.addEventListener('click', () => window.print());
    }

    const btnSearchAnother = document.getElementById('btnSearchAnother');
    if (btnSearchAnother) {
        btnSearchAnother.addEventListener('click', () => {
            resultContainer.classList.add('hidden');
            rollNumberInput.value = '';
            rollNumberInput.focus();
        });
    }
}

async function fetchStudentResult(rollNumber) {
    try {
        resultContainer.classList.add('hidden');
        notFoundCard.classList.add('hidden');

        const response = await fetch(`${API_BASE}/${encodeURIComponent(rollNumber)}`);
        const json = await response.json();

        if (response.ok && json.success && json.data) {
            renderMarkSheet(json.data);
            resultContainer.classList.remove('hidden');
        } else {
            notFoundMsg.textContent = json.message || `No student record found with Roll Number: ${rollNumber}`;
            notFoundCard.classList.remove('hidden');
        }
    } catch (err) {
        showToast('Error connecting to server. Please ensure the backend is running.');
    }
}

function renderMarkSheet(student) {
    document.getElementById('resStudentName').textContent = student.studentName;
    document.getElementById('resRollNumber').textContent = student.rollNumber;
    document.getElementById('resClass').textContent = student.gradeClass;
    document.getElementById('resEmail').textContent = student.email || 'N/A';

    const statusBadge = document.getElementById('resStatusBadge');
    if (student.status === 'PASS') {
        statusBadge.textContent = 'PASS';
        statusBadge.className = 'status-badge pass';
    } else {
        statusBadge.textContent = 'FAIL';
        statusBadge.className = 'status-badge fail';
    }

    // Subjects
    const subjects = [
        { code: 'MTH-101', name: 'Mathematics', marks: student.mathematics },
        { code: 'SCI-102', name: 'Science', marks: student.science },
        { code: 'ENG-103', name: 'English', marks: student.english },
        { code: 'CSC-104', name: 'Computer Science', marks: student.computerScience },
        { code: 'SST-105', name: 'Social Studies', marks: student.socialStudies }
    ];

    const tbody = document.getElementById('resMarksBody');
    tbody.innerHTML = '';
    subjects.forEach(sub => {
        const passed = sub.marks >= 35;
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td><strong>${sub.code}</strong> - ${sub.name}</td>
            <td>100</td>
            <td>35</td>
            <td><strong>${sub.marks}</strong></td>
            <td>
                <span class="marks-pill ${passed ? 'pill-pass' : 'pill-fail'}">
                    ${passed ? 'PASS' : 'FAIL'}
                </span>
            </td>
        `;
        tbody.appendChild(tr);
    });

    document.getElementById('resTotalMarks').textContent = `${student.totalMarks}`;
    document.getElementById('resPercentage').textContent = `${student.percentage}%`;
    document.getElementById('resGrade').textContent = student.grade;
    document.getElementById('resRemarks').textContent = student.remarks || 'Standard Performance';
}

// Admin / Records Management
async function loadDashboardStats() {
    try {
        const res = await fetch(`${API_BASE}/stats`);
        const json = await res.json();
        if (json.success && json.data) {
            const stats = json.data;
            if (statTotalStudents) statTotalStudents.textContent = stats.totalStudents;
            if (statPassedStudents) statPassedStudents.textContent = stats.passedStudents;
            if (statFailedStudents) statFailedStudents.textContent = stats.failedStudents;
            if (statPassRate) statPassRate.textContent = `${stats.passPercentage}%`;
            if (statHighestMarks) statHighestMarks.innerHTML = `${stats.highestMarks} <span class="sub-unit">/ 500</span>`;
        }
    } catch (e) {
        console.error('Failed to load stats', e);
    }
}

let cachedStudents = [];

async function loadAllStudents() {
    try {
        const res = await fetch(API_BASE);
        const json = await res.json();
        if (json.success && json.data) {
            cachedStudents = json.data;
            renderStudentTable(cachedStudents);
        }
    } catch (e) {
        console.error('Failed to load students', e);
    }
}

function renderStudentTable(students) {
    if (!studentTableBody) return;
    studentTableBody.innerHTML = '';

    if (students.length === 0) {
        studentTableBody.innerHTML = `
            <tr>
                <td colspan="8" style="text-align: center; color: var(--text-muted); padding: 2rem;">
                    No student records found. Click "+ Add New Result" to create one.
                </td>
            </tr>
        `;
        return;
    }

    students.forEach(s => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td class="font-mono"><strong>${s.rollNumber}</strong></td>
            <td>${s.studentName}</td>
            <td>${s.gradeClass}</td>
            <td><strong>${s.totalMarks}</strong> / 500</td>
            <td>${s.percentage}%</td>
            <td><strong style="color: var(--primary);">${s.grade}</strong></td>
            <td>
                <span class="marks-pill ${s.status === 'PASS' ? 'pill-pass' : 'pill-fail'}">
                    ${s.status}
                </span>
            </td>
            <td class="text-right">
                <div class="action-btn-group">
                    <button class="btn-icon" title="View Grade Card" onclick="viewStudentCard('${s.rollNumber}')">
                        <svg viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="currentColor" stroke-width="2"><path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7Z"/><circle cx="12" cy="12" r="3"/></svg>
                    </button>
                    <button class="btn-icon" title="Edit Result" onclick="editStudent(${s.id})">
                        <svg viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 20h9"/><path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z"/></svg>
                    </button>
                    <button class="btn-icon delete-btn" title="Delete Result" onclick="deleteStudent(${s.id}, '${s.rollNumber}')">
                        <svg viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
                    </button>
                </div>
            </td>
        `;
        studentTableBody.appendChild(tr);
    });
}

// Table search filter
if (tableSearchInput) {
    tableSearchInput.addEventListener('input', (e) => {
        const query = e.target.value.toLowerCase().trim();
        const filtered = cachedStudents.filter(s =>
            s.studentName.toLowerCase().includes(query) ||
            s.rollNumber.toLowerCase().includes(query) ||
            s.gradeClass.toLowerCase().includes(query)
        );
        renderStudentTable(filtered);
    });
}

// Modal handling
function initAdminModal() {
    if (btnOpenAddModal) {
        btnOpenAddModal.addEventListener('click', () => {
            modalTitle.textContent = 'Add New Student Result';
            resultForm.reset();
            formStudentId.value = '';
            formRollNumber.disabled = false;
            updateLiveCalculations();
            resultModal.classList.remove('hidden');
        });
    }

    const closeModal = () => resultModal.classList.add('hidden');
    if (btnCloseModal) btnCloseModal.addEventListener('click', closeModal);
    if (btnCancelModal) btnCancelModal.addEventListener('click', closeModal);

    // Save/Update form submit
    if (resultForm) {
        resultForm.addEventListener('submit', async (e) => {
            e.preventDefault();

            const payload = {
                rollNumber: formRollNumber.value.trim().toUpperCase(),
                studentName: formStudentName.value.trim(),
                gradeClass: formGradeClass.value.trim(),
                email: formEmail.value.trim() || null,
                mathematics: parseInt(formMath.value) || 0,
                science: parseInt(formScience.value) || 0,
                english: parseInt(formEnglish.value) || 0,
                computerScience: parseInt(formCS.value) || 0,
                socialStudies: parseInt(formSocial.value) || 0
            };

            const id = formStudentId.value;
            const url = id ? `${API_BASE}/${id}` : API_BASE;
            const method = id ? 'PUT' : 'POST';

            try {
                const res = await fetch(url, {
                    method,
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });
                const json = await res.json();

                if (res.ok && json.success) {
                    showToast(id ? 'Student result updated successfully!' : 'New student result created successfully!');
                    closeModal();
                    loadDashboardStats();
                    loadAllStudents();
                } else {
                    showToast(json.message || 'Error saving result');
                }
            } catch (err) {
                showToast('Failed to communicate with the server.');
            }
        });
    }
}

// Edit student
window.editStudent = function(id) {
    const student = cachedStudents.find(s => s.id === id);
    if (!student) return;

    modalTitle.textContent = `Edit Result - ${student.rollNumber}`;
    formStudentId.value = student.id;
    formRollNumber.value = student.rollNumber;
    formRollNumber.disabled = false;
    formStudentName.value = student.studentName;
    formGradeClass.value = student.gradeClass;
    formEmail.value = student.email || '';
    formMath.value = student.mathematics;
    formScience.value = student.science;
    formEnglish.value = student.english;
    formCS.value = student.computerScience;
    formSocial.value = student.socialStudies;

    updateLiveCalculations();
    resultModal.classList.remove('hidden');
};

// Delete student
window.deleteStudent = async function(id, roll) {
    if (!confirm(`Are you sure you want to delete student result for ${roll}?`)) return;

    try {
        const res = await fetch(`${API_BASE}/${id}`, { method: 'DELETE' });
        const json = await res.json();
        if (res.ok && json.success) {
            showToast(`Record for ${roll} deleted successfully.`);
            loadDashboardStats();
            loadAllStudents();
        } else {
            showToast(json.message || 'Failed to delete student.');
        }
    } catch (e) {
        showToast('Error deleting student.');
    }
};

// Quick view from admin table
window.viewStudentCard = function(rollNumber) {
    document.getElementById('navSearchTab').click();
    rollNumberInput.value = rollNumber;
    fetchStudentResult(rollNumber);
};

// Live calculation inside Add/Edit form
function initLiveCalculation() {
    [formMath, formScience, formEnglish, formCS, formSocial].forEach(inp => {
        if (inp) inp.addEventListener('input', updateLiveCalculations);
    });
}

function updateLiveCalculations() {
    const m = Math.min(100, Math.max(0, parseInt(formMath.value) || 0));
    const s = Math.min(100, Math.max(0, parseInt(formScience.value) || 0));
    const e = Math.min(100, Math.max(0, parseInt(formEnglish.value) || 0));
    const cs = Math.min(100, Math.max(0, parseInt(formCS.value) || 0));
    const ss = Math.min(100, Math.max(0, parseInt(formSocial.value) || 0));

    const total = m + s + e + cs + ss;
    const percentage = Math.round((total / 5.0) * 100.0) / 100.0;
    const passed = m >= 35 && s >= 35 && e >= 35 && cs >= 35 && ss >= 35 && percentage >= 40.0;

    let grade = 'F';
    if (!passed) grade = 'F';
    else if (percentage >= 90) grade = 'A+';
    else if (percentage >= 80) grade = 'A';
    else if (percentage >= 70) grade = 'B+';
    else if (percentage >= 60) grade = 'B';
    else if (percentage >= 50) grade = 'C';
    else grade = 'D';

    if (liveTotal) liveTotal.textContent = `${total} / 500`;
    if (livePercent) livePercent.textContent = `${percentage}%`;
    if (liveGrade) liveGrade.textContent = grade;
    if (liveStatus) {
        liveStatus.textContent = passed ? 'PASS' : 'FAIL';
        liveStatus.className = `status-badge-inline ${passed ? 'pass' : 'fail'}`;
    }
}
