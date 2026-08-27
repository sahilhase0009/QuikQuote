/**
 * QuoteFlow Dynamic Frontend Application Script
 */

document.addEventListener('DOMContentLoaded', function () {

    // --- QUOTATION BUILDER CALCULATIONS ---
    const itemsContainer = document.getElementById('quotation-items-container');
    const addItemBtn = document.getElementById('add-item-btn');

    if (itemsContainer) {
        // Initial binding
        bindItemRowEvents();
        calculateQuotationTotals();

        if (addItemBtn) {
            addItemBtn.addEventListener('click', function () {
                addNewItemRow();
            });
        }

        // Global discount input listener
        const discountValInput = document.getElementById('discountValue');
        const discountTypeInput = document.getElementById('discountType');

        if (discountValInput) discountValInput.addEventListener('input', calculateQuotationTotals);
        if (discountTypeInput) discountTypeInput.addEventListener('change', calculateQuotationTotals);
    }

    function bindItemRowEvents() {
        const rows = document.querySelectorAll('.item-row');
        rows.forEach(row => {
            const productSelect = row.querySelector('.product-select');
            const qtyInput = row.querySelector('.item-qty');
            const priceInput = row.querySelector('.item-price');
            const taxInput = row.querySelector('.item-tax');
            const discountInput = row.querySelector('.item-discount');
            const removeBtn = row.querySelector('.remove-row-btn');

            if (productSelect && !productSelect.dataset.bound) {
                productSelect.dataset.bound = "true";
                productSelect.addEventListener('change', function () {
                    const selectedOpt = productSelect.options[productSelect.selectedIndex];
                    if (selectedOpt && selectedOpt.value) {
                        const price = selectedOpt.dataset.price || '0.00';
                        const tax = selectedOpt.dataset.tax || '18.00';
                        const unit = selectedOpt.dataset.unit || 'Piece';
                        const name = selectedOpt.dataset.name || selectedOpt.text;
                        const desc = selectedOpt.dataset.desc || '';

                        const nameInput = row.querySelector('.item-name');
                        const descInput = row.querySelector('.item-desc');
                        const unitInput = row.querySelector('.item-unit');

                        if (nameInput) nameInput.value = name;
                        if (descInput) descInput.value = desc;
                        if (unitInput) unitInput.value = unit;
                        if (priceInput) priceInput.value = parseFloat(price).toFixed(2);
                        if (taxInput) taxInput.value = parseFloat(tax).toFixed(2);

                        calculateRowTotal(row);
                        calculateQuotationTotals();
                    }
                });
            }

            [qtyInput, priceInput, taxInput, discountInput].forEach(input => {
                if (input && !input.dataset.bound) {
                    input.dataset.bound = "true";
                    input.addEventListener('input', function () {
                        calculateRowTotal(row);
                        calculateQuotationTotals();
                    });
                }
            });

            if (removeBtn && !removeBtn.dataset.bound) {
                removeBtn.dataset.bound = "true";
                removeBtn.addEventListener('click', function () {
                    if (document.querySelectorAll('.item-row').length > 1) {
                        row.remove();
                        reindexItemRows();
                        calculateQuotationTotals();
                    } else {
                        alert("Quotation must have at least one item.");
                    }
                });
            }
        });
    }

    function calculateRowTotal(row) {
        const qty = parseFloat(row.querySelector('.item-qty')?.value || 1);
        const price = parseFloat(row.querySelector('.item-price')?.value || 0);
        const taxPct = parseFloat(row.querySelector('.item-tax')?.value || 0);
        const discount = parseFloat(row.querySelector('.item-discount')?.value || 0);

        let subtotal = (qty * price) - discount;
        if (subtotal < 0) subtotal = 0;

        let tax = subtotal * (taxPct / 100);
        let total = subtotal + tax;

        const subtotalEl = row.querySelector('.row-subtotal');
        const taxEl = row.querySelector('.row-tax');
        const totalEl = row.querySelector('.row-total');

        if (subtotalEl) subtotalEl.textContent = '₹' + subtotal.toFixed(2);
        if (taxEl) taxEl.textContent = '₹' + tax.toFixed(2);
        if (totalEl) totalEl.textContent = '₹' + total.toFixed(2);
    }

    function calculateQuotationTotals() {
        let grandSubtotal = 0;
        let grandTax = 0;

        const rows = document.querySelectorAll('.item-row');
        rows.forEach(row => {
            const qty = parseFloat(row.querySelector('.item-qty')?.value || 1);
            const price = parseFloat(row.querySelector('.item-price')?.value || 0);
            const taxPct = parseFloat(row.querySelector('.item-tax')?.value || 0);
            const discount = parseFloat(row.querySelector('.item-discount')?.value || 0);

            let sub = (qty * price) - discount;
            if (sub < 0) sub = 0;
            let tax = sub * (taxPct / 100);

            grandSubtotal += sub;
            grandTax += tax;
        });

        const discountValInput = document.getElementById('discountValue');
        const discountTypeInput = document.getElementById('discountType');

        let discountVal = parseFloat(discountValInput?.value || 0);
        let discountType = discountTypeInput?.value || 'PERCENTAGE';
        let discountAmt = 0;

        if (discountType === 'PERCENTAGE') {
            discountAmt = grandSubtotal * (discountVal / 100);
        } else {
            discountAmt = discountVal;
        }

        if (discountAmt > grandSubtotal) discountAmt = grandSubtotal;

        let taxableBase = grandSubtotal - discountAmt;
        if (taxableBase < 0) taxableBase = 0;

        if (grandSubtotal > 0 && discountAmt > 0) {
            grandTax = grandTax * (taxableBase / grandSubtotal);
        }

        let grandTotal = taxableBase + grandTax;

        // Render to UI
        setElText('summary-subtotal', '₹' + grandSubtotal.toFixed(2));
        setElText('summary-discount', '- ₹' + discountAmt.toFixed(2));
        setElText('summary-tax', '₹' + grandTax.toFixed(2));
        setElText('summary-total', '₹' + grandTotal.toFixed(2));
    }

    function setElText(id, text) {
        const el = document.getElementById(id);
        if (el) el.textContent = text;
    }

    function addNewItemRow() {
        const rows = document.querySelectorAll('.item-row');
        const newIndex = rows.length;
        const template = rows[0].cloneNode(true);

        // Reset inputs
        template.querySelectorAll('input, select, textarea').forEach(input => {
            input.removeAttribute('data-bound');
            if (input.tagName === 'SELECT') {
                input.selectedIndex = 0;
            } else if (input.classList.contains('item-qty')) {
                input.value = 1;
            } else if (input.classList.contains('item-price') || input.classList.contains('item-tax') || input.classList.contains('item-discount')) {
                input.value = '0.00';
            } else {
                input.value = '';
            }
        });

        itemsContainer.appendChild(template);
        reindexItemRows();
        bindItemRowEvents();
    }

    function reindexItemRows() {
        const rows = document.querySelectorAll('.item-row');
        rows.forEach((row, index) => {
            row.querySelectorAll('input, select, textarea').forEach(input => {
                const name = input.getAttribute('name');
                if (name) {
                    const newName = name.replace(/items\[\d+\]/, `items[${index}]`);
                    input.setAttribute('name', newName);
                }
            });
        });
    }

    // --- AI ASSISTANT PAGE AJAX ---
    const analyzeBtn = document.getElementById('analyze-enquiry-btn');
    if (analyzeBtn) {
        analyzeBtn.addEventListener('click', function () {
            const enquiryText = document.getElementById('enquiryText')?.value;
            const customerId = document.getElementById('ai-customer-select')?.value;

            if (!enquiryText || !enquiryText.trim()) {
                alert('Please enter a customer enquiry text.');
                return;
            }

            analyzeBtn.disabled = true;
            analyzeBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Analyzing with AI...';

            fetch('/api/ai/analyze-enquiry', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ enquiryText: enquiryText, customerId: customerId ? parseInt(customerId) : null })
            })
            .then(res => res.json())
            .then(data => {
                analyzeBtn.disabled = false;
                analyzeBtn.innerHTML = '<i class="bi bi-cpu me-2"></i>Analyze Enquiry';
                renderAiAnalysisResults(data, customerId);
            })
            .catch(err => {
                analyzeBtn.disabled = false;
                analyzeBtn.innerHTML = '<i class="bi bi-cpu me-2"></i>Analyze Enquiry';
                alert('AI Analysis failed: ' + err.message);
            });
        });
    }

    function renderAiAnalysisResults(data, customerId) {
        const container = document.getElementById('ai-results-container');
        if (!container) return;

        let itemsHtml = '';
        if (data.items && data.items.length > 0) {
            data.items.forEach((item, i) => {
                const statusBadge = item.matched 
                    ? `<span class="badge bg-success"><i class="bi bi-check-circle me-1"></i>Matched: ${item.matchedProductName} (₹${item.price})</span>`
                    : `<span class="badge bg-warning text-dark"><i class="bi bi-exclamation-triangle me-1"></i>${item.matchedProductName}</span>`;

                itemsHtml += `
                    <div class="p-3 border rounded mb-2 bg-light">
                        <div class="d-flex justify-content-between align-items-center">
                            <div>
                                <strong>${item.extractedName}</strong> &times; ${item.quantity} ${item.unit || 'Piece'}
                                <div class="text-muted small">${item.extractedDescription || ''}</div>
                            </div>
                            <div>${statusBadge}</div>
                        </div>
                    </div>
                `;
            });
        }

        let reqsHtml = '';
        if (data.specialRequirements && data.specialRequirements.length > 0) {
            reqsHtml = '<ul class="mb-0 text-muted">' + data.specialRequirements.map(r => `<li>${r}</li>`).join('') + '</ul>';
        }

        container.innerHTML = `
            <div class="card card-custom p-4 mb-4">
                <h5 class="fw-bold mb-3"><i class="bi bi-stars text-primary me-2"></i>Detected Requirements</h5>
                <p class="text-muted">${data.summary || 'Extracted requirement summary:'}</p>

                <div class="mb-3">${itemsHtml}</div>

                ${reqsHtml ? `<div class="mb-3"><h6>Special Requirements:</h6>${reqsHtml}</div>` : ''}

                <div class="mt-3 text-end">
                    <button id="create-draft-btn" class="btn btn-primary-custom">
                        <i class="bi bi-file-earmark-plus me-2"></i>Create Quotation Draft
                    </button>
                </div>
            </div>
        `;

        document.getElementById('create-draft-btn')?.addEventListener('click', function () {
            let url = '/quotations/new';
            if (customerId) url += '?customerId=' + customerId;
            window.location.href = url;
        });
    }

    // --- AI FOLLOW-UP GENERATOR MODAL AJAX ---
    const generateFollowupBtn = document.getElementById('generate-followup-btn');
    if (generateFollowupBtn) {
        generateFollowupBtn.addEventListener('click', function () {
            const quotationId = generateFollowupBtn.dataset.quotationId;
            const toneSelect = document.getElementById('followup-tone')?.value || 'Professional';
            const outputBox = document.getElementById('followup-message-box');

            if (!quotationId) return;

            generateFollowupBtn.disabled = true;
            generateFollowupBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Generating...';

            fetch('/api/ai/generate-followup', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ quotationId: parseInt(quotationId), tone: toneSelect })
            })
            .then(res => res.json())
            .then(data => {
                generateFollowupBtn.disabled = false;
                generateFollowupBtn.innerHTML = '<i class="bi bi-magic me-2"></i>Generate Message';
                if (outputBox) outputBox.value = data.message;
            })
            .catch(err => {
                generateFollowupBtn.disabled = false;
                generateFollowupBtn.innerHTML = '<i class="bi bi-magic me-2"></i>Generate Message';
                alert('Follow-up generation failed: ' + err.message);
            });
        });
    }

    const copyFollowupBtn = document.getElementById('copy-followup-btn');
    if (copyFollowupBtn) {
        copyFollowupBtn.addEventListener('click', function () {
            const outputBox = document.getElementById('followup-message-box');
            if (outputBox && outputBox.value) {
                navigator.clipboard.writeText(outputBox.value);
                copyFollowupBtn.innerHTML = '<i class="bi bi-check2 me-1"></i>Copied!';
                setTimeout(() => {
                    copyFollowupBtn.innerHTML = '<i class="bi bi-clipboard me-1"></i>Copy Message';
                }, 2000);
            }
        });
    }
});
