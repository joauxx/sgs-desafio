// Variáveis de Estado
let solicitacoes = [];
let categorias = [];
let solicitantes = [];

// Elementos do DOM
const tableBody = document.getElementById('table-body');
const tableLoading = document.getElementById('table-loading');
const tableEmpty = document.getElementById('table-empty');
const filterForm = document.getElementById('filter-form');
const filterCategoria = document.getElementById('filter-categoria');
const formNovaSolicitacao = document.getElementById('form-nova-solicitacao');
const selectSolicitante = document.getElementById('input-solicitante');
const selectCategoria = document.getElementById('input-categoria');
const toastContainer = document.getElementById('toast-container');

// Bootstrap Modal Instância (Variável Global)
let modalInstance = null;

// Inicialização
document.addEventListener('DOMContentLoaded', () => {
    carregarCategorias();
    carregarSolicitantes();
    carregarSolicitacoes();

    // Event Listeners
    filterForm.addEventListener('submit', (e) => {
        e.preventDefault();
        const formData = new FormData(filterForm);
        const params = {
            status: formData.get('status'),
            categoriaId: formData.get('categoriaId'),
            dataInicio: formData.get('dataInicio'),
            dataFim: formData.get('dataFim'),
        };
        carregarSolicitacoes(params);
    });

    formNovaSolicitacao.addEventListener('submit', async (e) => {
        e.preventDefault();
        const data = {
            solicitanteId: document.getElementById('input-solicitante').value,
            categoriaId: document.getElementById('input-categoria').value,
            descricao: document.getElementById('input-descricao').value,
            valor: parseFloat(document.getElementById('input-valor').value)
        };
        try {
            await api.post('/solicitacoes', data);
            showToast('Solicitação criada com sucesso!', 'success');
            closeModal('modal-nova-solicitacao');
            formNovaSolicitacao.reset();
            carregarSolicitacoes();
        } catch (error) {
            showToast('Erro ao criar solicitação: ' + error.message, 'error');
        }
    });
});

// Funções de Carregamento
async function carregarCategorias() {
    try {
        categorias = await api.get('/categorias');
        const options = categorias.map(c => `<option value="${c.id}">${c.nome}</option>`).join('');
        filterCategoria.innerHTML += options;
        selectCategoria.innerHTML += options;
    } catch (e) {
        showToast('Erro ao carregar categorias.', 'error');
    }
}

async function carregarSolicitantes() {
    try {
        solicitantes = await api.get('/solicitantes');
        const options = solicitantes.map(s => `<option value="${s.id}">${s.nome} (${s.cpfCnpj})</option>`).join('');
        selectSolicitante.innerHTML += options;
    } catch (e) {
        showToast('Erro ao carregar solicitantes.', 'error');
    }
}

async function carregarSolicitacoes(params = {}) {
    tableLoading.classList.remove('d-none');
    tableBody.innerHTML = '';
    tableEmpty.classList.add('d-none');

    try {
        const data = await api.get('/solicitacoes', params);
        const lista = data.content || data; // fallback
        solicitacoes = lista;

        renderTable(lista);
    } catch (e) {
        showToast('Erro ao carregar solicitações.', 'error');
    } finally {
        tableLoading.classList.add('d-none');
    }
}

// Renderização
function renderTable(lista) {
    if (lista.length === 0) {
        tableEmpty.classList.remove('d-none');
        return;
    }

    const formatCurrency = (value) => new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value);

    tableBody.innerHTML = lista.map((item, index) => {
        const btnActions = getActionButtons(item.id, item.status);

        return `
            <tr>
                <td class="py-3 px-4">
                    <div class="d-flex align-items-center">
                        <div class="rounded-circle bg-light text-brand fw-bold d-flex align-items-center justify-content-center me-3 border" style="width: 32px; height: 32px;">
                            ${item.nomeSolicitante.charAt(0)}
                        </div>
                        <div>
                            <div class="fw-medium text-dark small">${item.nomeSolicitante}</div>
                            <div class="text-muted small" style="font-size: 0.75rem;">${item.documentoSolicitante}</div>
                        </div>
                    </div>
                </td>
                <td class="py-3 px-4">
                    <div class="text-dark small">${item.nomeCategoria}</div>
                </td>
                <td class="py-3 px-4 fw-medium text-dark small">
                    ${formatCurrency(item.valor)}
                </td>
                <td class="py-3 px-4">
                    ${renderBadge(item.status)}
                </td>
                <td class="py-3 px-4 text-end">
                    <div class="d-flex align-items-center justify-content-end gap-1">
                        ${btnActions}
                    </div>
                </td>
            </tr>
        `;
    }).join('');
}

function renderBadge(status) {
    const config = {
        'SOLICITADO': 'text-bg-warning',
        'LIBERADO': 'text-bg-info text-white',
        'APROVADO': 'text-bg-success',
        'REJEITADO': 'text-bg-danger',
        'CANCELADO': 'text-bg-secondary'
    };
    const style = config[status] || 'text-bg-light';
    return `<span class="badge rounded-pill fw-normal px-2 py-1 ${style}">${status}</span>`;
}

function getActionButtons(id, status) {
    let actions = '';
    const btnClass = "btn btn-sm btn-light border-0 px-2 py-1";

    actions += `<button onclick="abrirDetalhes(${id})" title="Detalhes" class="${btnClass} text-secondary"><i class="ph ph-eye fs-5"></i></button>`;

    if (status === 'SOLICITADO') {
        actions += `<button onclick="alterarStatus(${id}, 'LIBERADO')" title="Liberar" class="${btnClass} text-info"><i class="ph ph-check-circle fs-5"></i></button>`;
        actions += `<button onclick="alterarStatus(${id}, 'REJEITADO')" title="Rejeitar" class="${btnClass} text-danger"><i class="ph ph-x-circle fs-5"></i></button>`;
    } else if (status === 'LIBERADO') {
        actions += `<button onclick="alterarStatus(${id}, 'APROVADO')" title="Aprovar" class="${btnClass} text-success"><i class="ph ph-check-fat fs-5"></i></button>`;
        actions += `<button onclick="alterarStatus(${id}, 'REJEITADO')" title="Rejeitar" class="${btnClass} text-danger"><i class="ph ph-x-circle fs-5"></i></button>`;
    } else if (status === 'APROVADO') {
        actions += `<button onclick="alterarStatus(${id}, 'CANCELADO')" title="Cancelar" class="${btnClass} text-secondary"><i class="ph ph-prohibit fs-5"></i></button>`;
    }

    if (status === 'REJEITADO' || status === 'CANCELADO') {
        actions += `<span class="text-muted ms-2" style="font-size: 0.75rem;">Finalizado</span>`;
    }

    return actions;
}

// Ações
async function alterarStatus(id, novoStatus) {
    if (!confirm(`Tem certeza que deseja mudar para ${novoStatus}?`)) return;
    try {
        await api.patch(`/solicitacoes/${id}/status`, { status: novoStatus });
        showToast(`Status atualizado para ${novoStatus}`, 'success');
        carregarSolicitacoes();
    } catch (e) {
        showToast(e.message, 'error');
    }
}

// UI Helpers (Bootstrap API)
function openModal(id) {
    const el = document.getElementById(id);
    modalInstance = new bootstrap.Modal(el);
    modalInstance.show();
}

function closeModal(id) {
    if (modalInstance) {
        modalInstance.hide();
    }
}

function showToast(message, type = 'success') {
    const isSuccess = type === 'success';
    const borderClass = isSuccess ? 'border-success' : 'border-danger';
    const iconClass = isSuccess ? 'ph-check-circle text-success' : 'ph-warning-circle text-danger';

    const toastHTML = `
        <div class="toast align-items-center bg-white border-0 shadow-sm border-start border-4 ${borderClass}" role="alert" aria-live="assertive" aria-atomic="true">
            <div class="d-flex">
                <div class="toast-body d-flex align-items-center gap-2">
                    <i class="ph ${iconClass} fs-5"></i>
                    <span class="fw-medium small">${message}</span>
                </div>
                <button type="button" class="btn-close me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
        </div>
    `;

    // Append to container
    toastContainer.insertAdjacentHTML('beforeend', toastHTML);
    const toastEl = toastContainer.lastElementChild;

    // Initialize and show via Bootstrap API
    const bsToast = new bootstrap.Toast(toastEl, { delay: 3000 });
    bsToast.show();

    // Clean up DOM after hidden
    toastEl.addEventListener('hidden.bs.toast', () => {
        toastEl.remove();
    });
}

// Detalhamento
async function abrirDetalhes(id) {
    try {
        const data = await api.get(`/solicitacoes/${id}`);

        document.getElementById('detalhe-solicitante-nome').textContent = data.nomeSolicitante;
        document.getElementById('detalhe-solicitante-doc').textContent = data.documentoSolicitante;
        document.getElementById('detalhe-categoria').textContent = data.nomeCategoria;

        if (data.dataSolicitacao) {
            const dateObj = new Date(data.dataSolicitacao);
            document.getElementById('detalhe-data').textContent = dateObj.toLocaleDateString('pt-BR');
        } else {
            document.getElementById('detalhe-data').textContent = '-';
        }

        const formatCurrency = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });
        document.getElementById('detalhe-valor').textContent = formatCurrency.format(data.valor);

        document.getElementById('detalhe-status').innerHTML = renderBadge(data.status);
        document.getElementById('detalhe-descricao').textContent = data.descricao;

        const modal = bootstrap.Modal.getOrCreateInstance(document.getElementById('modal-detalhes'));
        modal.show();
    } catch (e) {
        showToast('Erro ao carregar detalhes da solicitação.', 'error');
    }
}
