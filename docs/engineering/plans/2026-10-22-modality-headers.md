# Cabeçalhos de modalidades e especiais

Objetivo: reproduzir o cabeçalho realmente consumido por SimulaActivity/LotecaActivity do CX, `cabecalho_simula`, sem confundir com `fragment_barra_titulo_simulacao` da confirmação.

- [x] Reproduzir divergências de geometria e títulos em testes de UI; medir recurso no APK nativo offline.
- [x] Preservar 11 paletas/modalidades e fontes originais; corrigir geometria da simulação para16sp/ícones48dp/metadados centralizados.
- [x] Comparar os especiais MegaVirada, QuinaSãoJoão, LotofácilIndependência, DuplaPáscoa, LotecaEspecial, Mega30 e LotecaPais com o consumidor nativo efetivo.
- [x] Consumir configuração de concursos especiais sem bloquear a aposta ou executar operações financeiras.
- [x] Validar matriz de UI/fontes ampliadas, testes unitários, gates, app real semChucker; registrar resultados e limites.

Nenhuma aposta, pagamento ou reserva real. Reutilizar checkout e preservar migração existente. Compilar e usar adb de forma serial.
