from pathlib import Path
import json,re,datetime,xml.etree.ElementTree as ET,textwrap,sys
from xml.sax.saxutils import escape
from reportlab.pdfgen import canvas
from reportlab.platypus import SimpleDocTemplate,Paragraph,Spacer,PageBreak,Table,TableStyle,Image,KeepTogether,Preformatted,Flowable
from reportlab.lib.styles import getSampleStyleSheet,ParagraphStyle
from reportlab.lib.colors import HexColor,white
from reportlab.lib.enums import TA_CENTER
from reportlab.platypus.tableofcontents import TableOfContents
from zoneinfo import ZoneInfo
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
R=Path(__file__).resolve().parents[2]; D=R/'docs/technical-guide';OUT=R/'output/pdf/guia-tecnico-loterias-caixa.pdf'
edition=datetime.datetime.now(ZoneInfo('America/Sao_Paulo')).date()
months=('janeiro','fevereiro','março','abril','maio','junho','julho','agosto','setembro','outubro','novembro','dezembro')
EDITION_DATE=edition.strftime('%d/%m/%Y'); EDITION_LONG=f'{edition.day} de {months[edition.month-1]} de {edition.year}'
MIN_SDK=re.search(r'minSdk\s+(\d+)',(R/'app/build.gradle').read_text()).group(1)
AUDIT_PATH=D/'architecture-audit.json'
AUDIT=json.loads(AUDIT_PATH.read_text()) if AUDIT_PATH.exists() else None
API=json.loads((D/'api-contracts.json').read_text()); BLUE=HexColor('#005CA9');NAVY=HexColor('#004876');ORANGE=HexColor('#F39200');LIGHT=HexColor('#EDF4F7');INK=HexColor('#344752')
font=R/'core/design-system/src/main/res/font'
for n,f in [('Caixa','caixa_std_regular.ttf'),('CaixaBold','caixa_std_bold.ttf')]:pdfmetrics.registerFont(TTFont(n,str(font/f)))
pdfmetrics.registerFontFamily('Caixa',normal='Caixa',bold='CaixaBold',italic='Caixa',boldItalic='CaixaBold')
styles=getSampleStyleSheet()
styles.add(ParagraphStyle(name='B',fontName='Caixa',fontSize=10.2,leading=15,textColor=INK,spaceAfter=9))
styles.add(ParagraphStyle(name='H',fontName='CaixaBold',fontSize=23,leading=28,textColor=BLUE,spaceAfter=16))
styles.add(ParagraphStyle(name='EvidenceC',parent=styles['B'],fontSize=9.5,leading=13.5,spaceAfter=4))
styles.add(ParagraphStyle(name='S',fontName='CaixaBold',fontSize=13,leading=18,textColor=NAVY,spaceBefore=10,spaceAfter=8))
styles.add(ParagraphStyle(name='TinyC',fontName='Caixa',fontSize=8,leading=11,textColor=INK))
styles.add(ParagraphStyle(name='ContentsC',fontName='Caixa',fontSize=10,leading=15,textColor=INK,spaceBefore=7,leftIndent=10,firstLineIndent=-10))
styles.add(ParagraphStyle(name='TableHead',parent=styles['TinyC'],textColor=white,fontName='CaixaBold'))
styles.add(ParagraphStyle(name='CodeC',fontName='Courier',fontSize=7.2,leading=10,textColor=NAVY,backColor=LIGHT,borderPadding=9,spaceAfter=10))
story=[]
def p(s,sty='B'):return Paragraph(s,styles[sty])
def add(s,sty='B'):story.append(p(s,sty))
def heading(title):story.append(PageBreak());add(title,'H')
def code(s):
 lines=[]
 for line in s.splitlines():lines.extend(textwrap.wrap(line,100,replace_whitespace=False,drop_whitespace=False) or [''])
 for offset in range(0,len(lines),40):story.append(Preformatted('\n'.join(lines[offset:offset+40]),styles['CodeC']))
def source(s):add('Fonte: '+escape(s),'TinyC')
def table(rows,widths=None):
 t=Table([[p(escape(str(c)),'TableHead' if i==0 else 'TinyC') for c in row] for i,row in enumerate(rows)],colWidths=widths,repeatRows=1,hAlign='LEFT')
 t.setStyle(TableStyle([('BACKGROUND',(0,0),(-1,0),BLUE),('TEXTCOLOR',(0,0),(-1,0),white),('ROWBACKGROUNDS',(0,1),(-1,-1),[white,LIGHT]),('VALIGN',(0,0),(-1,-1),'TOP'),('TOPPADDING',(0,0),(-1,-1),7),('BOTTOMPADDING',(0,0),(-1,-1),7),('LINEBELOW',(0,0),(-1,0),1,ORANGE)]))
 story.append(t);story.append(Spacer(1,10))
class Diagram(Flowable):
 def __init__(self,rows):Flowable.__init__(self);self.rows=rows;self.width=490;self.height=len(rows)*53;self.spaceAfter=12
 def draw(self):
  c=self.canv
  for i,row in enumerate(self.rows):
   y=self.height-(i+1)*53;w=480/len(row)
   for j,label in enumerate(row):
    x=j*w;c.setFillColor(BLUE if i%2==0 else NAVY);c.roundRect(x,y,w-9,35,5,fill=1,stroke=0);c.setFillColor(white);c.setFont('CaixaBold',10);c.drawCentredString(x+(w-9)/2,y+13,label)
    if i<len(self.rows)-1:c.setStrokeColor(ORANGE);c.line(x+w/2,y-2,x+w/2,y-14)
# Capa desenhada pelo callback da primeira página
story.append(Spacer(1,205));add('DOCUMENTAÇÃO TÉCNICA','H');add('Android • Loterias CAIXA','S');add('Arquitetura, acessibilidade, Design System, segurança e contratos HTTP.');add('Inclui acesso aos dados por funcionalidade, serviços e endpoints, catálogo de APIs, arquitetura e evidências de qualidade.');story.append(Spacer(1,30));add('Edição técnica • '+EDITION_LONG,'S');add('Baseado no checkout local e em relatórios identificados por data. Não constitui contrato oficial do backend nem homologação de SDKs externos.','B')
heading('Controle do documento')
table([['Identificação','Valor'],['Documento','Guia técnico Android - Loterias CAIXA'],['Revisão',edition.strftime('%Y.%m.%d')],['Emissão',EDITION_DATE+' - America/Sao_Paulo'],['Base','Checkout local; inventários extraídos novamente dos fontes'],['Escopo','Análise estática, arquitetura, operação e catálogo de APIs'],['Situação','MVVM/Clean/ReduxKotlin; acessibilidade nas 14 features; auditoria integral'],['Limites','Resultados por execução; dívida global de Ktlint discriminada; sem homologação nova de release']], [105,385])
add('O template desta edição reutiliza o logotipo, as fontes CAIXA Std e a paleta institucional do aplicativo. Os achados e resultados técnicos refletem o checkout revisado, sem atribuir aprovação institucional ao documento.')
heading('Sumário')
toc=TableOfContents();toc.levelStyles=[styles['ContentsC']];story.append(toc)
heading('Como ler este documento')
add('O documento acompanha o código real. Exemplos de payload usam dados fictícios; tokens, hosts, certificados e arquivos de chaves são representados por variáveis. Nenhuma aposta, cobrança, reserva ou alteração de cadastro foi executada para produzi-lo.')
add('As implementações em desenvolvimento são separadas de resultados aprovados. Uma suíte parcial não substitui o qualityGate global, e a porcentagem de cobertura não demonstra correção de todos os fluxos financeiros ou visuais.')
heading('Novidades consolidadas - 28/10/2026')
add('Esta edição reúne funcionalidades e ferramentas entregues nas rodadas recentes. Os resultados vêm da execução completa do pre-commit de 28/10; a atualização documental não representa outra execução de testes nem nova homologação física.')
table([['Entrega','Comportamento e responsabilidade'],['Ícones de campanha','Calendário JSON anual, cinco presets, fallback padrão e avaliação também com o aplicativo fechado; quatro atalhos preservados.'],['ApostaShake e tutoriais','Leitura por ViewModel/Redux e operação Domain; seleção em BettingGameViewModel e persistência por porta/adaptador Data.'],['Auditoria arquitetural','App/Core/14 features; Domain/Data/DI, pureza dos reducers, ownership de operações, respostas antigas e navegação consumida uma vez.'],['Comentários e APIs Android','Padrão português para comentários de linha, KDoc e blocos; ajustes KTX String.toUri nos pontos Android, preservando os contratos.'],['Pre-commit e CI','Runner compartilhado, tarefa completa, bloqueio por qualquer falha ou divergência workspace/index; fixtures XML em cópias temporárias.'],['Resultados','3.510 testes Android/JVM; 190 de ferramentas, 21 de hooks e 33 contratos XML; JaCoCo 95,11%. Ktlint global reprovado, commit bloqueado.'],['Documentação','Inventários, manifestos atuais e históricos, JSON de campanhas, guias de arquitetura/Redux/qualidade e catálogo API extraído dos fontes.']], [105,385])
source('docs/UPDATES_20261028.md; docs/technical-guide/pre-commit-summary.json; docs/architecture/FULL_AUDIT_20261028.md. Publicação Firebase e validação física mantêm evidências próprias.')
heading('01  Tecnologia verificada')
stack=[['Camada','Tecnologia / versão no projeto'],['Build','Gradle 9.4.1; AGP 9.2.1; JDK 21; bytecode JVM 21'],['Linguagem e UI','Kotlin / Compose compiler 2.4.10; Compose BOM 2026.08.00; Material 3'],['Android',f'compileSdk e targetSdk 37; minSdk {MIN_SDK}'],['Estado e navegação','Lifecycle 2.11.0; Navigation 2.10.0; Coroutines 1.11.0; StateFlow; ReduxKotlin 0.6.2 (14 features)'],['Injeção','Hilt 2.60.1; KSP 2.3.6'],['HTTP / serialização','Retrofit 3.0.0; OkHttp 5.5.0; Gson 2.14.0'],['Autenticação / configuração','AppAuth 0.11.1; Firebase BOM 34.18.0'],['UI e leitura','Coil 3.6.1; Lottie 6.7.1; CameraX 1.6.2; ML Kit 17.3.0; ZXing 3.5.4'],['Persistência nova','DataStore Core 1.3.0; AES-GCM; Android Keystore'],['Qualidade','JUnit 4.13.2; Robolectric; Compose UI Test; JaCoCo 0.8.13; Detekt 1.23.8; Ktlint 1.8.0']];table(stack,[110,380]);source('gradle/libs.versions.toml; build.gradle; app/build.gradle; gradle/wrapper/gradle-wrapper.properties. Versões descrevem o checkout, sem alegação de serem as últimas disponíveis.')
heading('Feature First + Clean Architecture + MVVM')
add('<b>Feature First</b> organiza o código por capacidade do produto. <b>Clean Architecture</b> define os contratos e as fronteiras. <b>MVVM</b> organiza estado e ações da apresentação. Essas três decisões se complementam.')
story.append(Diagram([['APP: composição, bootstrap e navegação raiz'],['FEATURE: Presentation','FEATURE: Domain','FEATURE: Data'],['CORE: infraestrutura e contratos compartilhados'],['HTTP / Keystore / DataStore / SDKs']]))
add('Domain declara casos de uso, modelos e portas de repositório. Data implementa I/O, mapeamento e integrações. Presentation recebe ações, executa casos de uso e publica estado para Compose. A composição Hilt conhece implementações para conectar as portas; isso não autoriza telas a executar I/O diretamente.')
add('Os módulos api e transport limitam superfícies compartilhadas e reduzem ciclos. DTOs em transport podem conservar o namespace data.dto. O módulo marketplace-filter possui apenas presentation por reutilizar contratos existentes; não há módulos vazios apenas para simetria.')
source('ARCHITECTURE.md; docs/FEATURE_MVVM_CONTRACT.md; settings.gradle; feature/support/{presentation,domain,data}.')
heading('Fluxo concreto: tela de Ajuda')
story.append(Diagram([['SupportRoute / SupportScreen'],['SupportViewModel + StateFlow'],['GetSupportSections / SupportRepository'],['SupportRepositoryImpl / DataSource'],['LoteriasApiProvider / Retrofit / OkHttp']]))
add('SupportRoute coleta o estado com collectAsStateWithLifecycle e passa dados e callbacks à tela. SupportViewModel possui store Redux tipado e expõe StateFlow somente leitura. GetSupportSections depende de SupportRepository. A implementação converte o DTO do transporte em modelo de domínio.')
add('A resposta percorre o caminho de volta e produz recomposição. Direção das dependências e fluxo de execução são conceitos distintos: a implementação de Data depende do contrato de Domain, embora o caso de uso invoque essa implementação em runtime.')
add('Regras usam dependências testáveis. Por exemplo, SelectHomeResaleOffers recebe ServerTimeSource e aplica o encerramento inclusivo da oferta; não depende do relógio direto de um composable.')
source('feature/support/presentation/.../route/SupportRoute.kt; viewmodel/SupportViewModel.kt; feature/support/domain/.../usecase/GetSupportSections.kt; feature/home/domain/.../SelectHomeResaleOffers.kt.')
heading('Módulos e pacotes')
settings=(R/'settings.gradle').read_text();mods=re.findall(r"include\s+['\"](:[^'\"]+)['\"]",settings)
add(f'O settings declara {len(mods)} módulos nesta edição. A lista abaixo é gerada a partir do checkout; módulos pais apenas organizadores não devem ser interpretados como unidades com código próprio.')
rows=[['Grupo','Módulos']]
for prefix in ['app','core','sample']:
 rows.append([prefix,', '.join(m for m in mods if m.split(':')[1]==prefix)])
for f in sorted({m.split(':')[2] for m in mods if m.startswith(':feature:')}):rows.append([f,', '.join(m.split(':')[-1] for m in mods if m.startswith(':feature:'+f+':'))])
table(rows,[105,385])
code('feature/<feature>/presentation/.../{route,screen,viewmodel,state,components}\nfeature/<feature>/domain/.../{model,repository,usecase,rule,port}\nfeature/<feature>/data/.../{repository,datasource,mapper}\nfeature/<feature>/transport/.../data/dto\ncore/persistence/.../{EncryptedSnapshotSerializer,AndroidSecureStores}\napp/.../{composition,navigation,environment,runtime}')
heading('KISS, DRY e CID')
add('<b>KISS.</b> Estruturas acompanham necessidades reais: filtro reutiliza domínios existentes, estados ficam no ViewModel e adaptadores simples não ganham camadas vazias. Simplicidade exige manter as garantias de transação, migração e recuperação.')
add('<b>DRY.</b> CaixaDS centraliza tema, fontes, dimensões, ícones e componentes básicos. Formatação monetária e precedência de loading/erro/conteúdo são compartilhadas. Políticas de negócio diferentes permanecem separadas, mesmo quando têm aparência semelhante.')
add('<b>CID - Confidencialidade.</b> Minimização de dados, armazenamento autenticado criptografado, tokens sensíveis fora de exemplos e escopos por ambiente/dono. Keystore protege as chaves de uso local; não torna o dispositivo invulnerável.')
add('<b>CID - Integridade.</b> GCM autentica bytes e contexto; schemas e regras validam conteúdo; assinatura/quórum e versão crescente protegem keysets remotos; transações agrupam dados que precisam permanecer coerentes.')
add('<b>CID - Disponibilidade.</b> Escritas duráveis antes da confirmação visual, preservação do original durante migração e retenção de rascunhos em falhas. A recuperação deve reconciliar operações incertas em vez de repetir efeitos financeiros.')
source('docs/KISS_MODULE_REVIEW.md; docs/FINAL_ARCHITECTURE_DS_DRY_REVIEW.md; docs/superpowers/specs/2026-10-22-encrypted-datastore-recovery-design.md.')
heading('02  Caixa Design System e sample')
add('O DS reúne tokens, temas e componentes básicos reutilizáveis, incluindo cabeçalhos, campos, botões, ícones, estados e WebViews. Blocos específicos de uma tela continuam sob responsabilidade da feature, com dados visuais e callbacks para os componentes do DS.')
add('O CaixaDSTextField compartilha o renderer de entrada e oferece apresentações compatíveis com cada uso. Edição de conta conserva máscaras, campos desabilitados, alinhamento e validações do Legado. A padronização não deve remover regras do domínio.')
add('O aplicativo independente DS Loterias Caixa apresenta uma lista pesquisável de 121 APIs públicas Compose, famílias, detalhes, assinaturas, snippets e demonstrações interativas. Não inicializa autenticação ou backend do aplicativo principal. A demonstração Web usa conteúdo local e JavaScript desabilitado.')
code('./gradlew :sample:design-system:assembleDebug\n./gradlew :sample:design-system:testDebugUnitTest\n./gradlew :sample:design-system:installDebug')
add('O sample ajuda a avaliar estados, reproduzir problemas de componentes e mostrar o uso aos desenvolvedores antes de integrá-los às features. Testes locais validam navegação e interação; aparência de controles nativos também exige dispositivo/emulador.')
source('core/design-system/README.md; sample/design-system/README.md; CatalogRegistry.kt; CatalogActivityLaunchTest.kt.')
heading('Sample: catálogo de componentes')
story.append(Image(str(D/'images/sample-current.png'),width=205,height=205*2856/1280))
add('Captura de referência do sample em emulador Android 16, com o título DS Loterias Caixa. As ações de demonstração são locais e não realizam operações no backend.','TinyC')
heading('De x Para: correspondência com o Legado')
add('A correspondência é por responsabilidade e tela. Uma Activity antiga pode ter sido dividida em Route, Screen, ViewModel, UseCases e RepositoryImpl; não representa renomeação direta de classe. As fotos seguintes são evidências históricas com data e condições identificadas.')
mappings=json.loads((D/'legacy-mappings.json').read_text())
# Preserva os mapeamentos exatos auditados como texto estruturado sem inventar nomes de campos.
rows=[['Tela','Implementação atual','Legado']]
for entry in (mappings if isinstance(mappings,list) else mappings.get('mappings',[])):
 current=' / '.join(Path(entry[k]).name for k in ['route','viewmodel','data'] if entry.get(k))
 rows.append([entry.get('screen','Tela'),current,Path(entry.get('legacy','')).name])
table(rows,[105,250,135])
source('Correspondências auditadas nos caminhos reais do checkout e do projeto Legado. A coluna atual lista Route, ViewModel e adaptador quando presentes.')

def photos(title,left,right,caption,labels=('LEGADO','COMPOSE')):
 heading(title)
 paths=[R/left,R/right]
 if all(x.exists() for x in paths):
  ims=[]
  from PIL import Image as PILImage
  for path in paths:
   w,h=PILImage.open(path).size;ims.append(Image(str(path),width=220,height=220*h/w))
  story.append(Table([[p(labels[0],'S'),p(labels[1],'S')],ims],colWidths=[245,245]))
 add(caption,'TinyC')
photos('Comparativo: Bolões / lista','docs/technical-guide/images/legacy-pools.png','docs/technical-guide/images/compose-pools.png','Capturas de 15/10/2026, mesmo AVD Android 16, 1280 x 2856 e fixtures sintéticas equivalentes. Cabeçalho e barras de sistema podem refletir decisões posteriores. Estas imagens não comprovam paridade financeira ou a versão instalada hoje.')
photos('Comparativo: filtros de Bolões','docs/technical-guide/images/legacy-pool-filter.png','docs/technical-guide/images/compose-pool-filter.png','Mesma coleção controlada de 15/10/2026. Fonte: docs/MARKETPLACE_FILTER_PAIRED_PARITY_20261015.md. Comparar grupos, campos, seleção e ações; não inferir contratos de backend exclusivamente da imagem.')
photos('Comparativo: Ajuda','docs/technical-guide/images/legacy-support.png','docs/technical-guide/images/compose-support.png','Capturas públicas de 21/10/2026. Diferenças deliberadas de toolbar/insets são visíveis; não se afirma igualdade de pixels. As demais telas do índice não têm par fotográfico controlado validado nesta edição.')
heading('03  Persistência criptografada')
add('Encrypted DataStore descreve a combinação implementada no módulo core:persistence: DataStore Core grava snapshots serializados com criptografia autenticada AES-GCM. DataStore não fornece essa criptografia automaticamente. O Android Keystore fornece a chave; cada gravação usa IV de 12 bytes e tag de 128 bits.')
story.append(Diagram([['Feature: schema e regras de negócio'],['SecureRecordStore: read / update'],['DataStore: commit atômico de snapshot'],['Serializer AES-GCM + AAD'],['Arquivo privado noBackupFilesDir + Keystore']]))
add('SecureStoreScope contém ambiente, dono opaco e tipo do registro. O ambiente usa hash da identidade do backend e de seu namespace. Esses valores e a versão do envelope entram no AAD, impedindo que ciphertext de outro escopo seja aceito como o registro atual.')
add('Um registry mantém uma instância DataStore por arquivo durante o processo. O codec limita entradas, tamanhos e revisões. Arquivo corrompido ou chave ausente resulta em falha tipada; não é substituído silenciosamente por um documento vazio.')
add('Migração: ler e validar legado, gravar snapshot cifrado com marcador, reler e confirmar equivalência e somente então remover os registros antigos explicitamente capturados. Dados legados sem dono/ambiente verificável devem ficar em quarentena, sem atribuição automática a uma conta.')
source('core/domain/.../persistence/SecureRecordStore.kt; core/persistence/.../{AndroidSecureStores,FileSecureStores,EncryptedSnapshotSerializer,SnapshotBinaryCodec}.kt; plano de migração de 22/10/2026.')
heading('Escopo de armazenamento nesta edição')
table([['Dados','Situação e política'],['Carrinho visitante / transferência','Documento criptografado único, migração e quarentena de conteúdo sem vínculo verificável'],['Home / introdução / Shake','DataStore cifrado; commit antes da confirmação visual; tutorial por dono e ambiente'],['Limpeza de sessão / permissão de localização','Marcadores de instalação em DataStore cifrado, operações atômicas'],['Registro de push','Digest de confirmação por conta e ambiente, com lease capturada'],['Credencial biométrica','Proteção AES-GCM/Keystore já existente; migração desse adaptador para DataStore permanece pendente'],['Ambiente / cache público de keyset TLS','Adaptadores históricos ainda não migrados; assinatura do keyset protege integridade'],['Preferências de SDKs / exportações','Fora da migração automática; contratos externos e exportação explícita têm política própria']],[145,345])
add('Não se afirma que toda persistência do aplicativo já usa DataStore. O plano mantém etapas pendentes de bootstrap do ambiente, biometria, cache de keyset, flags de mock, switches de notificações e artefatos privados. A existência da fundação criptográfica não equivale à conclusão dessas migrações.')
heading('Recuperação: garantias e limites')
add('O rascunho é diferente de uma aposta submetida. BettingCheckpoint representa seleções parciais, etapa, modalidade, concurso, números, trevos, mês, time, placares, colunas, espelho, quantidades e edição de favorito. Uma fila de comandos deve persistir a projeção antes de publicar o estado confirmado à UI.')
add('A leitura exige o escopo correto e a lease atual da sessão. Na restauração, parâmetros e concurso são consultados novamente e validados. Não se restaura aprovação, pagamento concluído, sucesso de aposta, loading ou posição PASSED na fila.')
add('A intenção de uma operação remota é gravada antes da chamada. Após queda ou timeout, intenção sem resultado confirmado exige reconciliação. Não há reenvio automático de aposta, Pix, pagamento ou reserva. Rapidão não pode ser regenerado automaticamente apenas porque a rota reapareceu.')
add('O documento limita valores a 256 KiB e o snapshot a 1 MiB. Conteúdo que excede os limites não é truncado: a migração falha preservando o original e exige tratamento. A quarentena de dados sem dono/ambiente verificável também requer resolução explícita; não há atribuição automática a uma conta.')
add('O carrinho agrupa grafo, recibo de transferência, fingerprints e allocator na mesma transação. PENDING, CONFIRMED e CONFLICT conservam a prevenção de repetição e a remoção apenas dos itens cujo conteúdo foi confirmado.')
add('A reconciliação de intenção incerta ainda não tem resolução explícita completa em runtime. O rascunho permanece preservado e o reenvio fica bloqueado; reabrir o app encaminha à consulta do carrinho. Não se afirma que essa consulta, por si só, confirme a operação no servidor.')
add('Limites: não há contrato comprovado no SDK FilaBRA para exportar/importar posição na fila. Guardar o rascunho não garante conservar a posição no servidor. Preferências de SDKs externos, documentos exportados conscientemente pelo usuário e persistência do backend têm contratos próprios.')
source('feature/betting/domain/.../recovery; feature/betting/data/.../recovery; feature/cart/data/.../datasource/local; docs/superpowers/plans/2026-10-22-encrypted-datastore-implementation.md. Conferir status de integração e testes na seção de resultados.')
heading('04  Remote Config: atualização obrigatória')
add('Crie force_update como parâmetro Boolean, com padrão false. Quando o valor ativo for true, o app apresenta uma tela bloqueante antes do login. Voltar não fecha essa tela; o botão abre a loja por market:// e usa HTTPS como alternativa. O parâmetro não faz comparação automática de versão mínima.')
table([['Parâmetro','Tipo / padrão','Finalidade'],['force_update','Boolean / false','Bloquear entrada e encaminhar à loja'],['app_launcher_icon','String JSON / calendário anual','Selecionar campanha por período e retornar ao padrão'],['tls_pinning_keyset','JSON assinado','Rotacionar pins de hosts permitidos com validação local']],[120,115,255])
add('Use condições e audiência adequadas no Firebase para atingir versões instaladas. Publique primeiro em ambiente de teste e valide tanto o estado ativo quanto falha de fetch. A integração conserva o valor ativo em falhas; sem inicialização válida, o fallback de force_update é false.')
add('Push, configuração de funcionalidades e pinagem compartilham a instância Firebase padrão. A configuração usa timeout de 10 s e fetch(0) na inicialização e retomada; enquanto o app está em primeiro plano, uma recuperação a cada 30 s complementa o listener de tempo real. O ViewModel evita buscas sobrepostas e pausa o ciclo ao sair do estado RESUMED. A pinagem conserva a validação assinada e seu intervalo mínimo de 3.600 s. Falhas preservam os valores ativados; rede e throttling podem atrasar a atualização.')
source('Implementações Firebase de configuração no app; core/key-management; implementações locais Firebase de configuração, sem reprodução de valores secretos.')
heading('Remote Config: cinco ícones de campanha')
add('O parâmetro String app_launcher_icon contém um JSON com default_icon, timezone e campaigns. Cada campanha informa icon, start e end em MM-DD, com limites inclusivos e repetição anual. Agosto Lilás, Setembro Amarelo, Outubro Rosa e Novembro Azul usam os períodos configurados. Entradas sem datas válidas são ignoradas; JSON/fuso inválido ou ausência de campanha ativa resulta no ícone default.')
add('A leitura reutiliza o cliente Firebase padrão e seus valores ativados. Ativações por streaming, recuperação em primeiro plano e busca das chaves notificam os observadores do mesmo cliente. O caso de uso informa o ViewModel, o Redux incrementa a revisão do calendário e o App aplica o alias e os quatro atalhos sem recriar a tela atual. O calendário é reavaliado na abertura, no retorno ao app e por um alarme diário inexato, inclusive com o app fechado. Boot, mudança de relógio/fuso e atualização do APK reprogramam a avaliação. O receiver aguarda cache/defaults sem fetch de rede, respeita cancelamento e finaliza goAsync; operações nativas são serializadas na thread principal. Economia de bateria pode adiar o alarme; parada forçada do app exige nova abertura.')
add('Os recursos são empacotados no APK e cinco activity-alias apontam para a MainActivity habilitada. O controlador conserva os quatro IDs, rótulos, ícones e destinos públicos dos atalhos. Limitação de publicação adia a troca; a seleção já aplicada não é republicada sem necessidade.')
add('A prévia DEBUG launcher_icon_preview continua aceitando um nome de preset para validação local; Release ignora esse extra. A chave remota usa o JSON completo. Os defaults XML e config/firebase/app_launcher_icon.json são equivalentes. Automação/TQS usam o projeto NPRD 75059847302; PRD usa 1090799638737. A publicação precisa corresponder à variante instalada. O guia de ícones registra os templates publicados, a validação da release ofuscada e as limitações observadas; uma prévia local não comprova entrega remota.')
story.append(Image(str(D/'images/five-launcher-icons-comparison.png'),width=490,height=490*790/1700))
source('docs/technical-guide/launcher-icons.md; docs/superpowers/specs/2026-10-27-launcher-icons-remoteconfig-design.md. Artes adaptativas ilustrativas; as capturas reais e a matriz da rodada ficam em build/verification/launcher-icons-remoteconfig-20261027/.')
heading('Calendário anual: JSON completo')
code(json.dumps(json.loads((R/'config/firebase/app_launcher_icon.json').read_text()),ensure_ascii=False,indent=2))
add('Use este objeto completo como valor String de app_launcher_icon no Remote Config. Os períodos são anuais e inclusivos; fora deles o fallback é default. A definição local está empacotada no APK. A alteração remota exige publicação com acesso ao projeto. Sua validação precisa registrar o template recebido, o processo e o primeiro plano do aplicativo; uma configuração aplicada ao retomar não comprova entrega com o app continuamente aberto.')
source('config/firebase/app_launcher_icon.json; app/src/main/res/xml/remote_config_defaults.xml; docs/technical-guide/launcher-icons.md.')
launcher_capture=D/'images/samsung-five-icons-and-default-return.png'
if launcher_capture.exists():
 heading('Samsung: cinco ícones e retorno ao padrão')
 from PIL import Image as PILImage
 with PILImage.open(launcher_capture) as im: capture_width,capture_height=im.size
 story.append(Image(str(launcher_capture),width=490,height=490*capture_height/capture_width))
 add('Recortes das capturas reais do Samsung SM-G556B, launcher Microsoft. Sequência: padrão, Outubro Rosa, Setembro Amarelo, Novembro Azul, Agosto Lilás e retorno ao padrão. Rodada local DEBUG com o valor de configuração informado na inicialização; não houve publicação na nuvem.','TinyC')
 source('build/verification/launcher-icons-remoteconfig-20261027/: PNGs originais, XMLs do launcher, logs do controlador, aliases e matriz de atalhos fechado/aberto.')
shortcut_capture=D/'images/samsung-four-shortcuts-cold-warm.png'
if shortcut_capture.exists():
 heading('Samsung: quatro atalhos, fechado e aberto')
 with PILImage.open(shortcut_capture) as im: capture_width,capture_height=im.size
 story.append(Image(str(shortcut_capture),width=445,height=445*capture_height/capture_width))
 add('Oito cenários exercitados pelo menu real do launcher: quatro com o processo encerrado e quatro com o processo em execução. Mega-Sena, Lotofácil e Loteca abriram resultados públicos; Todos os resultados abriu a seleção de modalidades. Nos cenários abertos, o PID foi conservado.','TinyC')
 source('shortcuts-samsung.json e PNGs originais em build/verification/launcher-icons-remoteconfig-20261027/. Capturas com a configuração de fonte do dispositivo.')
heading('Pinagem: fallback, assinatura e confiança')
add('SIMLO_TLS_DEFAULT_PINS incorpora pins públicos de SPKI como fallback gerado pelo build. SIMLO_KEYSET_TRUSTED_SIGNERS contém as chaves públicas autorizadas a assinar novas configurações. São identidades diferentes: o pin autentica o servidor TLS; o signatário autoriza a mudança dessa política.')
add('O parâmetro tls_pinning_keyset recebe o envelope completo: payload em Base64URL e signatures com keyId/value. O payload inclui versão, issuedAtEpochSeconds, expiresAtEpochSeconds e hosts. O app valida assinatura/quórum, validade, hosts permitidos e versão estritamente crescente antes de aceitar a configuração.')
add('Sem âncoras de signatários autorizados, a rotação remota fica desabilitada e o fallback permanece aplicável.')
add('Configuração inválida conserva o conjunto confiável. Um cache expirado mantém a versão de proteção contra rollback, embora a política de pins retorne ao fallback aplicável. Chaves privadas e credenciais privilegiadas nunca pertencem ao APK, Git ou Remote Config.')
add('Gradle e R8 não tornam valores embarcados secretos. Pins, certificados e chaves públicas precisam estar disponíveis ao cliente. A ofuscação reduz exposição de símbolos, mas não substitui criptografia, assinatura ou controle de credenciais.')
add('A pinagem protege os clientes OkHttp conectados à política. Não cobre automaticamente Firebase, browser AppAuth, WebViews ou transportes internos de Topaz/FilaBRA. Certificado válido na cadeia TLS, hostname e pinagem são verificações complementares; não há proteção universal contra MITM em todos os SDKs apenas por configurar este parâmetro.')
heading('Estrutura do keyset assinado')
add('O envelope abaixo é apenas um esquema legível. Strings de demonstração não são assinaturas válidas e não devem ser publicadas. O arquivo gerado pelo script contém os valores Base64URL completos e assinaturas verificáveis.')
code('{"payload":"BASE64URL_DO_PAYLOAD","signatures":[{"keyId":"root-a","value":"BASE64URL_DA_ASSINATURA_A"},{"keyId":"root-b","value":"BASE64URL_DA_ASSINATURA_B"}]}')
add('Conteúdo do payload depois de decodificado (exemplo sintético):')
code('{"version":42,"issuedAtEpochSeconds":1792627200,"expiresAtEpochSeconds":1795219200,"hosts":[{"hostname":"api.exemplo.invalid","pins":["sha256/SPKI_BASE64_EXEMPLO"]}]}')
add('O valor no Firebase é o envelope assinado inteiro. A versão deve aumentar; os horários são segundos Unix, não strings de data. O hostname deve estar na allowlist da versão instalada. O valor ilustrativo do pin não é válido e o domínio .invalid é intencional.')
heading('Rotação de certificado: procedimento')
add('1. Infraestrutura entrega o certificado público .cer e informa hostname, cadeia e janela de troca. Mantenha as chaves privadas dos signatários em diretório externo ao projeto e a qualquer repositório Git, com permissões restritas e backup seguro.')
code('umask 077\n# SIGNER_A_FILE deve apontar para um caminho externo ao projeto/Git.\nopenssl genpkey -algorithm EC -pkeyopt ec_paramgen_curve:P-256 \\\n  -aes-256-cbc -out "$SIGNER_A_FILE"\n# Repita para um segundo signatário independente.')
add('2. Configure somente as chaves públicas autorizadas no build. Por padrão, o quórum é dois. A emissão do envelope deve usar o conjunto de signatários esperado pela versão instalada do app.')
add('3. Execute o script com o certificado novo e o último envelope publicado. O script preserva pins existentes, acrescenta o novo SPKI, incrementa a versão e assina o resultado. As senhas das chaves são fornecidas por variáveis de ambiente, sem registrar valores no comando.')
code('python3 scripts/rotate-remote-config-pins.py \\\n  --certificate "$PUBLIC_CERT_FILE" --hostname "$APPROVED_HOST" \\\n  --previous "$LATEST_PUBLISHED_ENVELOPE" \\\n  --signer "root-a=$SIGNER_A_FILE" --signer "root-b=$SIGNER_B_FILE" \\\n  --passphrase-env root-a=SIGNER_A_PASSPHRASE \\\n  --passphrase-env root-b=SIGNER_B_PASSPHRASE \\\n  --output "$PUBLIC_OUTPUT/tls-pins-next.json" \\\n  --update-local-defaults --properties "$PUBLIC_BUILD_PROPERTIES"')
add('4. Publique o conteúdo integral do JSON como valor JSON de tls_pinning_keyset. O script não publica no Firebase. Valide a aceitação em dispositivos antes de trocar o certificado do servidor. Conserve pins de transição; remoção exige outra configuração revisada com versão maior.')
add('5. --update-local-defaults também atualiza SIMLO_TLS_DEFAULT_PINS para builds futuros. Variáveis do CI/Gradle prevalecem sobre o arquivo local e precisam ser atualizadas separadamente. Valide cadeia, hostname e janela com Infra; o script não substitui a verificação operacional da cadeia TLS.')
source('DataStore 1.3.0 declarado em gradle/libs.versions.toml; scripts/rotate-remote-config-pins.py; scripts/sign-remote-config-pins.py. Arquivos privados reais e pins da instalação não são reproduzidos neste documento.')
heading('05  Chucker e diagnóstico')
add('Chucker 4.3.1 permite inspecionar transações HTTP dos clientes instrumentados: URL, método, headers, status, duração, tamanho e corpos disponíveis. A notificação e o atalho abrem o histórico local, útil para identificar parâmetros incorretos, falhas de contrato e diferenças entre requests.')
code('./gradlew :app:assemblePrdDebug -PnetworkLogging=true\n# Padrão seguro usado nos testes comuns:\n./gradlew :app:assemblePrdDebug -PnetworkLogging=false')
add('A dependência existe em debug e a fábrica release retorna null. O flag efetivo é networkLogging, false por padrão. Um flag de flavor chamado ENABLE_NETWORK_INSPECTOR existe, mas não governa sozinho a injeção; não assuma que prdDebug nunca pode ativar o inspector.')
add('Retenção configurada: uma hora; limite de conteúdo: 64.000 bytes. Authorization, Cookie, Set-Cookie, X-Api-Key e ocp-apim-subscription-key são ocultados. URLs e JSON passam por heurísticas de sanitização; corpos grandes, não JSON, one-shot ou duplex têm restrições.')
add('O interceptor de cURL do Logcat mantém o corpo como [BODY REDACTED]. Uma cópia sanitizada de request pode ser apresentada ao inspector sem alterar a requisição enviada. O decoder de response atua na apresentação: não é prova de que todo corpo bruto foi eliminado da persistência interna do Chucker.')
add('Fluxo recomendado: reproduzir com dados sintéticos em debug, filtrar a chamada, comparar o contrato deste catálogo, verificar status/mensagem e exportar apenas conteúdo revisado. Não compartilhar tokens, CPF, cartão ou dados de apostas. SDKs com transporte próprio podem não aparecer.')
source('core/network/.../logging; core/network/.../interceptor/CurlLoggingInterceptor.kt; core/security/src/{debug,release}; configuração NetworkClientCoordinator.')
heading('06  Rotina de qualidade para DEVs')
code("export JAVA_HOME='/Applications/Android Studio.app/Contents/jbr/Contents/Home'\n./gradlew ktlintCheck\n./gradlew detekt\n./gradlew lintQualityCheck\n./gradlew quickQualityCheck\n./gradlew allUnitTests\n./gradlew verifyCoverage\n./gradlew qualityGate\n./gradlew preCommitCheck\n./gradlew verifyFeatureModules verifyAppArchitecturePolicy\n./gradlew modernCheck\n./gradlew releaseCheck")
add('quickQualityCheck reúne formatação, análise estática e Android Lint. qualityGate acrescenta testes e cobertura global. modernCheck amplia montagem e verificadores arquiteturais/DS/segurança; releaseCheck verifica a versão release com R8. Build debug aprovado não prova release aprovado.')
add('Detekt opera no perfil sintático configurado para esta toolchain; não presuma resolução completa de tipos. Baselines registram dívida existente. Passar o gate significa não introduzir violações adicionais fora da dívida aceita, não ausência absoluta de problemas.')
add('A cobertura é LINE ponderada: soma de linhas cobertas dividida pela soma de linhas elegíveis. A condição é estritamente maior que 95%; exatamente 95% falha. Não se usa média dos percentuais dos módulos. Fontes manuais de UI, modelos e DTOs permanecem elegíveis; gerados têm exclusões próprias.')
code('build/reports/quality/full-summary.json\nbuild/reports/jacoco/aggregate/summary.json\nbuild/reports/jacoco/aggregate/jacoco.xml\nbuild/reports/jacoco/aggregate/html/index.html\nbuild/reports/jacoco/modules/<modulo>/jacoco.xml\n<modulo>/build/test-results/<tarefa>/TEST-*.xml\n<modulo>/build/reports/lint-results-*.html')
add('Existem supressões locais documentadas em funções declarativas de UI e cenários de geometria já existentes, incluindo LongMethod/CyclomaticComplexMethod. A formatação expôs essa dívida; as supressões são pontuais. O código novo de recuperação não recebeu supressões de complexidade. Baselines e limites de cobertura não foram ampliados para aprovar a migração.')
heading('Pre-commit completo e CI')
code('sh scripts/install-git-hooks.sh\n# Depois de preparar o conteúdo completo no staging:\npython3 scripts/validate-pre-commit.py\n# A chamada Gradle fixa do runner:\n./gradlew preCommitCheck --continue --console=plain')
add('O hook nativo scripts/git-hooks/pre-commit chama o runner Python do checkout atual. Ele verifica workspace e index antes e depois do gate. Alterações fora do staging, arquivos não rastreados/não ignorados, mudanças durante a execução, ferramentas ausentes ou qualquer falha impedem o commit. O instalador preserva hooks de terceiros e core.hooksPath; clones novos exigem instalação explícita.')
table([['Fronteira','Validações selecionadas'],['Qualidade global','Ktlint, Detekt, Android Lint, allUnitTests fresco e JaCoCo LINE estritamente maior que 95%.'],['Arquitetura e UI','Propriedade de fontes/grafo acíclico, App/Feature, Compose, inventário UI, backend e CaixaDS.'],['Plataforma','Identidade/flavors, rede, FilaBra/TLS e Topaz/geração de chaves.'],['Ferramentas','190 testes Python; 21 testes de hooks com Git real em repositórios temporários.'],['Recursos XML','33 cenários positivos/negativos em cópias temporárias; os fontes reais permanecem intactos.'],['Entrega local','Relatório agregado de testes e montagem do APK PRD Debug.']], [105,385])
add('O processo continua pelas verificações independentes para completar o diagnóstico; qualquer falha mantém saída diferente de zero. O runner não aceita opções de atalho, não prepara arquivos e não gera baseline ou formatação. A execução integral registrada levou 47min33s; duração depende do equipamento e dos caches.')
source('scripts/validate-pre-commit.py; scripts/git-hooks/pre-commit; scripts/install-git-hooks.sh; gradle/{quality-gate,compose-platform-resources}.gradle; docs/quality/hooks-and-ci.md.')
heading('Pre-commit: resultado registrado e limites')
precommit=json.loads((D/'pre-commit-summary.json').read_text())
table([['Item','Resultado'],['Android/JVM',str(precommit['junit']['tests'])+' aprovados; zero falhas, erros ou ignorados'],['Ferramentas Python',str(precommit['pythonToolingTests'])+' aprovados'],['Hook / contratos XML',str(precommit['gitHookTests'])+' testes e '+str(precommit['platformXmlChecks'])+' verificações com sucesso'],['JaCoCo LINE',precommit['coverageLine']+'% - 54.340 / 57.135 linhas'],['Ktlint global','FAILED: '+str(precommit['globalKtlint']['errors'])+' ocorrências em '+str(precommit['globalKtlint']['files'])+' arquivos'],['Processo completo','Código 1 exclusivamente por :ktlintCheck; commit bloqueado'],['Proteções','37 configurações/baselines e dois headers intactos']], [140,350])
add('O hook está instalado no checkout usado na execução. O teste direto encontrou conteúdo fora do staging e retornou erro antes do Gradle. Os testes descartáveis comprovam tanto bloqueios quanto commit permitido após aprovação, sem alterar o index do projeto real. A revisão independente não encontrou Critical/Important verificável no mecanismo.')
add('O workflow Android Quality Gate usa o mesmo runner e preserva bootstrap por secrets e publicação de relatórios em falha. Para impedir integração sem aprovação, o administrador precisa tornar o status obrigatório na proteção da branch/fila. Git permite desabilitar hooks locais; esta tarefa não configurou proteção remota nem executou CI remoto.')
add('Instrumentação física, TalkBack, Firebase publicado e release/R8 mantêm validações próprias. Os números acima foram transcritos do log completo existente; o trabalho documental não os reexecuta. Os testes de políticas presentes também nas dependências do gate não são somados outra vez como casos únicos.')
source('build/verification/pre-commit-20261028/{full-gate.log,final-summary.json,integrity-final.json,independent-review.json}; docs/technical-guide/pre-commit-summary.json.')
heading('Resultados de testes e cobertura')
# O manifesto de evidências só é criado após verificação real; caso contrário, mostra explicitamente o escopo histórico.
evidence_path=D/'verification.json'
if evidence_path.exists():evidence=json.loads(evidence_path.read_text())
else:evidence={'status':'EM VALIDAÇÃO - migração atual não homologada','historical':'Último gate global aprovado: 22/10/2026 19:13 America/Sao_Paulo. 95,48% (42.452 / 44.460 linhas), 66 módulos, 2.980 testes, 586 suítes, zero falhas/erros/skips. Esses resultados precedem a migração atual e não são cobertura atual.','current':'core:persistence: 9 testes passaram. Rodadas intermediárias identificaram schema desconhecido de carrinho e confirmação visual antes de gravação; correções em validação.'}
quality_keys=['Status','Testes unitários','Android instrumentado','Samsung','TalkBack','Carrinho','Qualidade','Proteções','Rastreabilidade','Pre-commit','Atualização documental','Limites','Histórico']
for k in quality_keys:
 if k in evidence:add('<b>'+escape(k)+':</b> '+escape(str(evidence[k])),'EvidenceC')
add(str(evidence.get('Cobertura', 'Cobertura global e release/R8 conservam o escopo histórico discriminado no manifesto.')))
source('docs/technical-guide/{verification,coverage-summary,quality-summary,unit-tests-by-module}.json e relatórios Gradle identificados no quadro acima. Não foram reduzidos limites ou regeneradas baselines para produzir aprovação.')
current_tests=D/'unit-tests-by-module.json'
if not current_tests.exists():current_tests=R/'build/verification/samsung-a11y-14features-20261027/unit-tests-final.json'
if not current_tests.exists():current_tests=R/'build/verification/a11y-full-audit-20261027/unit-tests-final.json'
if current_tests.exists():
 current=json.loads(current_tests.read_text())
 heading('Consolidado da rodada: testes por módulo')
 add(str(current.get('scope', 'Seleção histórica; a proveniência e as variantes são discriminadas no manifesto.')))
 table([['Módulo','Testes','Falhas','Ignorados']]+[[m['module'],m['tests'],m['failures'],m['skipped']] for m in current['modules']], [280,70,70,70])
 source(str(current_tests.relative_to(R))+'; tarefas test, testDebugUnitTest e App testPrdDebugUnitTest. Proveniência de execução/cache discriminada por módulo no JSON.')
coverage_path=D/'coverage-summary.json'
if evidence_path.exists() and coverage_path.exists():
 coverage=json.loads(coverage_path.read_text())
 heading(('Cobertura atual por módulo - ' if coverage.get('current') else 'Cobertura histórica por módulo - ') + coverage.get('edition','22/10/2026'))
 add(str(coverage.get('scope', 'Histórico de 22/10/2026; não é cobertura atual.')) + ' Módulos sem linhas executáveis aparecem como não aplicáveis; a condição global usa a soma ponderada, não a média dos percentuais.')
 rows=[['Módulo','Cobertas / elegíveis','LINE %']]
 for m in coverage['modules']:
  rows.append([m['module'],str(m['covered'])+' / '+str(m['eligible']),str(round(float(m['percentage']),2))+'%' if m['eligible'] else 'N/A'])
 table(rows,[320,105,65])

heading('07  Revisão arquitetural desta edição')
if AUDIT:
 add(str(AUDIT['scope']))
 add('<b>Estado:</b> '+escape(str(AUDIT['status'])))
 table([['ID','Responsabilidade corrigida']]+[[item['id'],item['correction']] for item in AUDIT['findings']], [70,420])
 source('docs/architecture/FULL_AUDIT_20261028.md; docs/technical-guide/architecture-audit.json; build/verification/architecture-audit-20261028/.')
else:
 add('A auditoria integral atual examinou App/Core e as 14 features, seus contratos, dependências, estado, efeitos e fronteiras de plataforma. Oito desvios confirmados receberam correções. As correções anteriores ARQ-01/02 são preservadas como histórico e continuam cobertas por regressões. A revisão independente atual não apontou novo P2 arquitetural verificável. Dois achados de acessibilidade receberam regressões específicas; um risco de traversal foi confirmado por Tab/Enter antes da correção.')
 table([['ID','Responsabilidade corrigida'],['FULL-01','Home possui resultados/cache/jobs no ViewModel e Redux; gerações rejeitam resposta antiga e expiração respeita visibilidade.'],['FULL-02','BetPricing/BetSelectionEligibility em Domain; lookup compartilhado preserva ausência/fallback, recorrência e mirror.'],['FULL-03','CartReservationExpiry em Domain; timezone e limites exatos/segundos preservados.'],['FULL-04','Notifications: leitura inicial e mutação+releitura em fila FIFO.'],['FULL-05','Tickets: política Pix pendente em Domain, sem coordinator importar Screen.'],['FULL-06','Support: conclusões/falhas/finally correlacionados à geração ativa.'],['FULL-07','Resumo do carrinho: DestinationSnapshotViewModel compartilhado e reducer tipado; composição só cria/observa/dispara refresh.'],['FULL-08','Onboarding: diálogos de armazenamento consomem os controles base do CaixaDS.']], [70,420])
add('Arestas Gradle foram avaliadas por escopo: testImplementation e referências Jacoco não foram interpretadas como dependências de produção. Repositório de sessão, cache de dados e protocolo transitório de SDK conservam seus donos e não recebem stores artificiais. Os gates são guardrails lexicais complementares à revisão e aos testes.')
table([['Achado','Correção','Contrato preservado'],['ARQ-01 / corrigido','ForceUpdateViewModel e Redux em Onboarding Presentation; operações/contrato em Domain; Firebase em Data. Fábrica da DI da feature; App só compõe.','Valor ativado em falha, cancelamento, observação, refresh sem duplicação e ordem do gate.'],['ARQ-02 / corrigido','Coordenador de entrega sem Android/Compose; metadados Redux e bytes privados. Uri, launcher e provedor ficam em Route.','Rotação, recuperação por nome/destino, IO, retry/cancel, reutilização e confirmação consumida uma vez.']], [95,205,190])
add('<b>Regressões de fronteira.</b> App não pode declarar ViewModels em runtime ou outros pacotes, nem clientes Remote Config ou dependência direta em Data de features. ViewModels das features não podem depender de Route/Screen/Components. Alias e referências qualificadas são verificados; análise lexical não prova todas as dependências transitivas futuras.')
add('<b>Recuperação.</b> O teste existente revelou uma diferença durante a integração: a falha de recuperação precisa ser publicada também quando a chamada vem diretamente do contrato do ViewModel. A guarda foi corrigida preservando as assertivas, e o fechamento ainda impede callbacks tardios.')
source('docs/architecture/{FULL_AUDIT_20261027,REVIEW_20261027}.md; build/verification/a11y-full-audit-20261027/{app-core-audit,features-a-audit,features-b-audit,independent-review}.md.')
heading('Alterações recentes do aplicativo')
table([['Área','Comportamento documentado'],['Atalhos do launcher','Mega-Sena, Lotofácil, Loteca e Todos os resultados; ícones próprios e destinos públicos fixos. Pedidos aguardam os gates normais e preservam navegação na abertura e no retorno.'],['Transições','Host compartilhado com avanço, pop e retorno preditivo sem fade; conteúdo e headers não são alterados pela política.'],['Carrinho visitante','Binding escolhe escopo de visitante somente sem identidade e sem token. Exemplo validado: 4 apostas, R$ 19,00.'],['Autenticação','Observação de conectividade respeita RESUMED para não interromper o SSO que permanece no navegador.'],['Estados LTR','LTR-001 e LTR-503 usam componente compartilhado e azul Blue700 do cabeçalho, com código e mensagem próprios.']], [105,385])
add('Evidências de dispositivo destas alterações são de 26 e 27/10/2026, produzidas em etapas anteriores a esta revisão. Não são reapresentadas como uma nova execução de testes nesta edição. Os atalhos e as transições têm regressões no módulo App.')
source('build/verification/{result-shortcuts-20261026,legacy-transitions-20261026,shortcut-icons-20261027}; app/runtime/shortcuts; app/navigation/{shortcuts,transition}; LoginNetworkObservation.kt.')

heading('ApostaShake, comentários e APIs Android')
add('ShakeTutorialRoute apresenta o tutorial e entrega a conclusão por callback. O sensor e a reação de UI pertencem aos adaptadores de apresentação; a aplicação da seleção é coordenada por BettingGameViewModel. A View recebe dados e callbacks; estado e efeitos mantêm seus proprietários nos ViewModels existentes.')
add('A leitura necessária para entrar no destino fica em BettingNavigationPreferencesViewModel e Redux, usando GetShakeEnabledUseCase/GetShakeEnabled de Betting Domain. ShakeSettingsRepository declara as operações; ShakeSettingsRepositoryImpl implementa a persistência em Data. Leitura, falha, nova tentativa e invalidação de resposta antiga têm proprietário e lifecycle explícitos.')
add('O padrão de comentários adotado é português em linhas, KDoc e blocos /* ... */. A tradução trata comentários e preserva identificadores, recursos, contratos e textos funcionais. Nos pontos Android ajustados, String.toUri usa a extensão KTX sem substituir a validação e o tratamento de falhas das rotas existentes.')
source('Betting Presentation: ShakeTutorialRoute.kt, BettingShakeEffect.kt, BettingNavigationPreferencesViewModel.kt e BettingGameViewModel.kt; Betting Domain/Data; Core Auth e links das rotas Android.')
heading('08  ReduxKotlin com MVVM nas 14 features')
add('As 14 features usam stores Redux tipados locais aos ViewModels. A ponte ReduxStateStore, em Core Presentation, concentra observação, sincronização e encerramento. AccountStatusViewModel coordena somente efeitos e não recebe store artificial. Estados independentes continuam separados; não existe store global.')
story.append(Diagram([['View: SupportRoute / SupportAction'],['SupportViewModel: efeitos e lifecycle'],['SupportStateStore: dispatch tipado'],['SupportReducer: estado + action -> novo estado'],['StateFlow somente leitura -> Compose']]))
add('ReduxKotlin é a autoridade de escrita. O StateFlow privado apenas projeta o estado publicado pelo store, mantendo o contrato de observação da tela. Dispatch concorrente e atualização da projeção são serializados. O fechamento é idempotente e remove a subscription no encerramento do ViewModel.')
source('feature/support/presentation/.../state/{SupportMutation,SupportReducer,SupportStateStore}.kt; viewmodel/SupportViewModel.kt; docs/REDUX_MVVM.md.')
heading('Legado, responsabilidades e contratos compartilhados')
add('O legado examinado usa reducers próprios em Java: SimulaViewModel e LotecaViewModel recebem eventos, aplicam reducers ao UiState e publicam LiveData observado pela Activity. Não foi encontrada dependência org.reduxkotlin nos fontes/Gradle examinados. A nova integração preserva esse princípio de propriedade e usa a biblioteca para store e dispatch, com StateFlow para a UI Compose.')
table([['Responsabilidade','Local'],['Transição pura de estado','Reducers de cada feature em presentation/state; sem Android, I/O, relógio ou UseCase'],['Efeitos e eventos de consumo único','ViewModels/coordenadores; casos de uso e entregas únicas ficam fora do reducer'],['Regras e adaptadores','Domain e Data conservam responsabilidades e não dependem de Redux'],['Observação e lifecycle','Store privado ao ViewModel; projeção StateFlow read-only e unsubscribe no encerramento'],['Persistência e diagnóstico','Sem persistência automática, logging de estado, DevTools ou replay de operações remotas']], [135,355])
add('<b>Compatibilidade.</b> Os artefatos estáveis são redux-kotlin-threadsafe-jvm e redux-kotlin-jvm 0.6.2, centralizados no Version Catalog e declarados com api em Core Presentation. O core JVM é explícito: a declaração thread-safe exclui o core multiplataforma transitivo para evitar que os metadados do Gradle selecionem o core Android novamente. AAR Android dessa versão contém bytecode JVM21 e falhou no inline do projeto JVM17; os artefatos JVM oficiais usam JVM17. Os artefatos estáveis continuam os mesmos, sem adotar a linha alpha. Atualmente todos os módulos do projeto compilam com toolchain e bytecode JVM21.')
code("api(libs.redux.kotlin.threadsafe) {\n    exclude group: 'org.reduxkotlin', module: 'redux-kotlin'\n}\napi libs.redux.kotlin.core")
add('<b>SOLID, KISS e DRY.</b> O reducer transforma dados, o store adapta threading/observação e o ViewModel coordena efeitos. As transições ficam em um único local. A ponte compartilhada evita repetir adaptação de Flow e lifecycle nas 14 features. O gate permite reducers somente nos pacotes state dos donos registrados e rejeita MutableStateFlow nas features. Não há ações genéricas de transformação ou substituição.')
add('<b>Concorrência.</b> Redux serializa transições; cancelamento e identidade de requisição são responsabilidades explícitas. Support rejeita respostas de gerações anteriores após trocar/fechar aba ou seção. Home aplica geração e cancelamento; Notifications preserva a ordem das escritas e releituras. O resumo de carrinho fecha seu dono ao sair/trocar fonte. Provedores não cooperativos são cobertos por regressões controladas.')
source('docs/REDUX_MVVM.md; docs/superpowers/plans/2026-10-27-redux-all-features.md; scripts/check-feature-architecture.py; https://github.com/reduxkotlin/redux-kotlin; artefatos oficiais em repo.maven.apache.org/maven2/org/reduxkotlin/.')


heading('Stores, ações e durabilidade')
if AUDIT:
 table([['Feature','ViewModels com Redux / total']]+[[item['name'],str(item['redux_viewmodel_count'])+' / '+str(item['viewmodel_count'])] for item in AUDIT['inventory']['features']], [300,190])
 add('Inventário atual: '+str(AUDIT['inventory']['feature_viewmodel_count'])+' ViewModels de features, '+str(AUDIT['inventory']['feature_redux_viewmodel_count'])+' com Redux e '+str(AUDIT['inventory']['feature_reducer_file_count'])+' arquivos de reducers. AccountStatus continua dedicado a efeitos. A extração de declarações de Actions não elimina transições de estado.')
else:
 table([['Feature','ViewModels com Redux / total'],['account', '7 / 8'],['betting', '6 / 6'],['cart', '4 / 4'],['home', '2 / 2'],['marketplace', '2 / 2'],['marketplace-filter', '1 / 1'],['notifications', '2 / 2'],['onboarding', '5 / 5'],['payment', '4 / 4'],['responsible-gaming', '1 / 1'],['results', '2 / 2'],['retail', '3 / 3'],['support', '1 / 1'],['tickets', '2 / 2']], [300,190])

add('Account, Betting, Cart, Home, Marketplace, Marketplace Filter, Notifications, Onboarding, Payment, Responsible Gaming, Results, Retail, Support e Tickets usam ações semânticas imutáveis. Os ViewModels registram os stores com addCloseable. A API da ponte contém apenas state, value, dispatch e close; não oferece setter, update ou closure de transformação.')
add('Leitura, dispatch e encerramento compartilham o mesmo monitor. Consumos de identidade e leitura seguida de confirmação usam synchronized(store), preservando a atomicidade anterior. Clock, sorteio, I/O, cancelamento e eventos únicos permanecem fora dos reducers. StateFlow é conflado e não é fila de eventos.')
add('Betting mantém estado de trabalho e estado confirmado na fila durável. Após persistir o candidato, DraftCommitted aplica as ações capturadas ao estado confirmado. O replay não repete operações remotas. CheckpointRestored e RestoreConfirmedDraft representam recuperação e rollback concretos. A confirmação de favorito avalia a revisão ao alcançar a fila, depois das seleções pendentes.')
source('core/presentation/.../state/ReduxStateStore.kt; feature/*/presentation/.../state/*Reducer.kt; BettingDraftCommands.kt; docs/REDUX_MVVM.md; build/verification/redux-all-20261027/.')

heading('09  Acessibilidade nas 14 features')
add('Referência confirmada: WCAG 2.2 AA, WCAG2ICT e recomendações Android. Esta edição descreve implementação e evidências; não declara certificação formal nem homologação integral de conteúdos/SDKs externos.')
add('CaixaDS respeita a escala de texto do sistema e mantém nomes/erros nos editáveis, papéis/seleção em escolhas, links nativos com callback e ações alcançáveis em diálogos. A roda expõe uma faixa finita e operação por teclado, sem multiplicar alvos pelas cópias visuais. WebView adapta textZoom na criação/recomposição e conserva DOM e política de origem/JavaScript. Cores de corpo corrigidas não alteram tokens de headers.')
table([['Feature','Aceitação específica'],['Account','Consentimento nomeado e link nativo dos termos.'],['Betting','Combo identificado, seleção Rapid/favoritos, placar contextual e rodas nomeadas.'],['Cart','Nome de favorito escalável e expansão anunciada; header preservado.'],['Home','Resultados escaláveis; corpo rolável e ações/rodapé em espaços separados na fonte ampliada.'],['Marketplace','Quantidade contextual e seleção do carrossel.'],['Marketplace Filter','Modalidade selecionada; mínimo/máximo e busca preenchidos nomeados, erro vinculado.'],['Notifications','Preferências em um alvo nomeado, tabs e expansão.'],['Onboarding','Preferência associada ao checkbox do tutorial.'],['Payment','Consentimento Pix associado ao estado e ação.'],['Responsible Gaming','Pergunta/período selecionável e confirmação nomeada.'],['Results','Busca preenchida nomeada; Super Sete coluna/dígito legível em 200%.'],['Retail','Tab Pix selecionada, nome/erro do campo e Enter uma vez.'],['Support','Link FAQ nativo com política/confirmacão existente; HTML escalável.'],['Tickets','NSB/Lotérico nomeados; acerto, coluna e premiação associados.']], [105,385])
heading('Verificação e limites de acessibilidade')
add('A inspeção visual no Samsung em 200% encontrou corte/sobreposição no cartão e no resumo do carrinho. O corpo ampliado usa rolagem e ações/rodapé em espaços medidos; o resumo cresce naturalmente. O caminho normal e os headers foram preservados.')
add('Regressões usam componentes reais e dados sintéticos: entrada vazia/preenchida/erro/senha/read-only/disabled, estado de escolha, callback único, texto ampliado e viewport curto. As dimensões visuais isoladas não provam área de toque: expansão Material, sobreposição e operação efetiva precisam ser consideradas. Conteúdo decorativo não recebe fala redundante.')
add('A revisão independente encontrou dois P2 de acessibilidade: campos de filtro sem nome/erro associado ao editável e Cancelar do Rapidão com tamanho fixo que corta texto em 200%. Foram acrescentados testes específicos antes das correções. Tab/Enter também confirmou foco duplicado em indicador filho; o filho decorativo deixa de ter ação própria quando a linha controla a escolha. O cenário estreito de suspensão foi mantido como regressão, separado de uma falha observada. Verificação nativa de glyphs demonstrou WebView em 19px nas duas escalas antes da correção. Resultados finais e limites estão no manifesto; uma árvore semântica aprovada não certifica a experiência falada do TalkBack.')
source('docs/ACCESSIBILITY.md; build/verification/a11y-full-audit-20261027/; https://www.w3.org/TR/WCAG22/; https://www.w3.org/TR/wcag2ict/; developer.android.com/develop/ui/compose/accessibility/{semantics,api-defaults,testing}.')
if (D/'images/a11y-cart-round-1.png').exists() and (D/'images/a11y-cart-round-2.png').exists():
 photos('Histórico Samsung: carrinho em duas rodadas', 'docs/technical-guide/images/a11y-cart-round-1.png', 'docs/technical-guide/images/a11y-cart-round-2.png', 'Auditoria anterior, antes da rodada autenticada complementar. APK instalado sem limpar dados. As duas rodadas conferem o conteúdo medido antes da instalação; totais e limitações constam no manifesto. Nenhuma compra, exclusão ou reposição de apostas.', labels=('RODADA 1','RODADA 2'))
if (D/'images/a11y-home-scale1.png').exists() and (D/'images/a11y-home-scale2.png').exists():
 photos('Histórico Samsung: preferência de tamanho do texto', 'docs/technical-guide/images/a11y-home-scale1.png', 'docs/technical-guide/images/a11y-home-scale2.png', 'Mesmo aplicativo no Samsung, com fonte normal e ampliada para 200%. Estilo dos headers preservado; escala natural do texto do sistema. A configuração temporária é restaurada após a validação.', labels=('FONTE NORMAL','FONTE 200%'))

if (D/'images/a11y-home-scale2-scrolled.png').exists():
 photos('Histórico Samsung: conteúdo ampliado com rolagem', 'docs/technical-guide/images/a11y-home-scale2.png', 'docs/technical-guide/images/a11y-home-scale2-scrolled.png', 'Fonte em 200%: o corpo do cartão rola até a data e o encerramento. As ações e o resumo do carrinho conservam seus espaços e não cobrem o conteúdo. A contagem e o total permanecem 5 apostas/R$22.', labels=('INÍCIO DO CORPO','CORPO APÓS ROLAR'))

# A validação complementar no dispositivo mantém a auditoria histórica separada.
validation_path=D/'samsung-a11y-summary-20261027.json'
if validation_path.exists():
 validation=json.loads(validation_path.read_text())
 heading('Histórico Samsung: rodada de 27/10/2026')
 add('Mesmo aplicativo no SM-G556B, TalkBack oficial e fonte de sistema em 200%. Capturas demonstram layout; confirmações humanas registram o roteiro solicitado e não certificam toda a fala. Toques ADB e árvores de testes não substituem exploração com leitor de tela.')
 positive=validation['positiveResponses']
 partial=validation['partialResponses']
 add(f'{positive} respostas positivas foram registradas para roteiros específicos; Cart e Results tiveram confirmação explícita de leitura completa e correta. Há {partial} respostas com limitações relatadas durante os retestes. Requisitos iniciais e respostas de autenticação não são contados como aprovações. A matriz registra o estado atual de cada critério, sem transformar relatos históricos em falhas atuais ou aprovações globais.')
 add('Visitante local: 5/R$22. Carrinho autenticado do servidor: 2/R$7, medido após entrada normal. São contextos distintos; não houve reenvio, exclusão, compra ou avaliação enviada. Entradas locais controladas no APK existente não usam fixture autenticada e não aprovam FilaBRA, primeira instalação ou fluxo remoto de compra; ForceUpdate e recuperação de sessão permanecem ativos.')

 status_labels={'PASSED':'Aprovado','PARTIAL':'Parcial','NOT_EXECUTED':'Não executado','BLOCKED':'Indisponível','FAILED':'Falhou'}
 add('<b>Critérios:</b> '+ '; '.join(str(v)+' '+status_labels.get(k,k).lower() for k,v in validation.get('totals',{}).items())+'.')
 add('Os 56 critérios são mais amplos que uma única tela. PARTIAL indica evidência de parte do roteiro; NOT_EXECUTED não é ignorado. PASSED aprova somente o cenário descrito. A rodada continua aberta enquanto houver reteste ou confirmação sem evidência suficiente.')
 heading('Matriz Samsung: critérios por feature')
 table([['Feature','Foco / operação','Fonte 200%','Integração']]+[[r['feature'],status_labels.get(r['checks'][0]['status'])+' / '+status_labels.get(r['checks'][1]['status']),status_labels.get(r['checks'][2]['status']),status_labels.get(r['checks'][3]['status'])] for r in validation['checks']],[95,170,110,115])
 heading('Leitura integral em português: Cart e Results')
 add('Cada bloco de aposta tem descrição completa e contínua, com numerais e moeda por extenso. Colunas do Super Sete, trevos, repetição, Loteca, cotas e Combo conservam o contexto. Números mascarados da Surpresinha não são revelados e identificadores preservam zeros. Excluir, detalhes e legenda mantêm ações próprias.')
 add('Resultados usa blocos de sorteio, faixa de premiação, partida, recompensa, ganhadores por região, próximo concurso e arrecadação. Busca, navegação e rolagem/paginação continuam independentes. O formatador puro compartilhado em Core Presentation não acrescenta dependência nem efeitos ao estado.')
 table([['Informação visual','Exemplo de leitura'],['R$ 3,50','três reais e cinquenta centavos'],['26 out 2026','vinte e seis de outubro de dois mil e vinte e seis'],['Coluna 1: 0, 2','Coluna um: zero, dois'],['Código A001','A zero zero um']],[180,310])
 add('PortugueseSpeechTest, CartWholeBlockSpeechTest e ResultWholeBlockSpeechTest verificam o texto e as ações. Paridade visual usa a árvore não fundida para medir os mesmos glifos e posições; as descrições agrupadas possuem asserções próprias na árvore do leitor. O APK final foi instalado, a sessão real retomou e o carrinho permaneceu 2/R$7. O usuário confirmou no Samsung, separadamente para Carrinho e Resultados: “Leitura completa e correta”. A confirmação se limita aos blocos e ações do roteiro realizado.')
 source('Core Presentation/accessibility/PortugueseSpeech.kt; mappers de apresentação de Cart/Results; docs/superpowers/specs/2026-10-27-cart-portuguese-readout-design.md; build/verification/samsung-a11y-14features-20261027/.')
 heading('Samsung: correções e cenários pendentes')
 add('Cortes reproduzidos receberam regressões antes da correção: login, modalidades de resultados, opções/preços, botões do Design System, rodapé/contador do carrinho, período de apostas, carrossel/detalhe de bolões, filtros, jogo responsável e categorias de FAQ. A entrada Home→scanner foi corrigida para permitir o caminho manual mesmo sem câmera. A introdução recebeu rodapé medido e corpo rolável em 200%. As cinco etapas de Bolões e sete de +Milionária receberam instruções equivalentes à animação e checkbox com nome/estado em um único alvo; o relato de falha e o reteste físico ficam separados. Mercado Pago recebeu escala real e nome do e-mail preenchido, verificados com fixture de prêmio elegível.')
 add('As correções são de apresentação. ViewModels e stores ReduxKotlin continuam coordenando o estado; regras e operações permanecem nas portas existentes. Não foram adicionados efeitos a reducers nem regras de negócio a componentes visuais. O menu da Home trata Voltar enquanto aberto, preservando a confirmação de saída quando fechado. Os ícones da introdução têm nomes localizados: Celular, Estrela e Lista com lupa. Anterior/Próximo usam o callback de página do ViewModel e têm rodapé medido na fonte ampliada; a conclusão permanece na ação original. Headers e baselines protegidos conservam seus hashes.')
 add('Títulos truncados de headers em 200% permanecem documentados para cumprir a restrição de não alterá-los. Primeiro uso não foi reiniciado; Home V2 está desativada nesta configuração. Resgate real exige prêmio elegível. Nenhuma transação é executada para preencher a matriz.')
 heading('Samsung: retestes por feature')
 for r in validation['checks']:add('<b>'+escape(r['feature'])+':</b> '+escape(r.get('remaining','')),'EvidenceC')
 source('docs/accessibility/SAMSUNG_VALIDATION_20261027.md; build/verification/samsung-a11y-14features-20261027/{matrix,manual-confirmations,observations,cart-contexts}.json. Datas de cada ação em actions.jsonl.')
 for key,title in [('cart','Carrinho: resumo e contador'),('marketplace','Bolões: texto e ações'),('filter','Filtros: dígitos completos'),('results','Resultados: modalidades completas')]:
  left='docs/technical-guide/images/samsung200-'+key+'-before.png';right='docs/technical-guide/images/samsung200-'+key+'-after.png'
  if (R/left).exists() and (R/right).exists():photos(title,left,right,'Samsung, fonte 200%, TalkBack ativo. Correção do corpo; headers preservados. Capturas não comprovam a experiência auditiva. Comparar o conteúdo/controle destacado, não posições ou dados de backend de instantes diferentes.',labels=('ANTES','DEPOIS'))


def append_data_access_guide():
 # O guia funcional é fonte única para Markdown e a edição PDF existente.
 text=(R/'docs/DATA_ACCESS_BY_FEATURE.md').read_text()
 text=text.replace('→',' -> ').replace('—','-').replace('–','-').replace('‑','-')
 # Os mesmos DTOs já estão integralmente no Apêndice B.
 text=re.sub(r'### Dicionário dos corpos e retornos tipados\n.*?(?=### Conferência e atualização deste inventário)', '', text, flags=re.S)
 styles.add(ParagraphStyle(name='GuideTable',parent=styles['TinyC'],fontSize=8.1,leading=11.2,wordWrap='CJK'))
 styles.add(ParagraphStyle(name='GuideTableHead',parent=styles['TableHead'],fontSize=8.1,leading=11.2,wordWrap='CJK'))
 def inline(value):
  # Referências internas permanecem legíveis no PDF, sem links para anchors Markdown.
  value=re.sub(r'\[([^\]]+)\]\([^)]+\)',r'\1',value)
  value=escape(value.replace('\u2011','-'))
  value=re.sub(r'`([^`]+)`',r'<font color="#004876">\1</font>',value)
  value=re.sub(r'\*\*([^*]+)\*\*',r'<b>\1</b>',value)
  return value
 def functional_table(lines):
  rows=[]
  for line in lines:
   cells=re.split(r'(?<!\\)\|',line.strip().strip('|'))
   if all(re.fullmatch(r'\s*:?-+:?\s*',c) for c in cells):continue
   rows.append([Paragraph(inline(c.strip()),styles['GuideTableHead' if not rows else 'GuideTable']) for c in cells])
  cols=len(rows[0]); widths={2:[155,335],3:[115,170,205],4:[115,120,145,110]}[cols]
  t=Table(rows,colWidths=widths,repeatRows=1,hAlign='LEFT')
  t.setStyle(TableStyle([('BACKGROUND',(0,0),(-1,0),BLUE),('ROWBACKGROUNDS',(0,1),(-1,-1),[white,LIGHT]),('VALIGN',(0,0),(-1,-1),'TOP'),('TOPPADDING',(0,0),(-1,-1),6),('BOTTOMPADDING',(0,0),(-1,-1),6),('LINEBELOW',(0,0),(-1,0),1,ORANGE)]))
  story.extend([t,Spacer(1,10)])
 heading('10  Dados, classes e serviços por funcionalidade')
 add('Integração do guia funcional ao documento técnico existente. Inclui entradas recomendadas de Domain, serviços e endpoints, parâmetros efetivos, retornos e fontes. O dicionário completo de DTOs permanece no Apêndice B. Esta edição documental não executa chamadas remotas nem renova resultados de testes.')
 lines=text.splitlines();i=0
 while i<len(lines):
  line=lines[i]
  if line.startswith('# '):i+=1;continue
  if line.startswith('```'):
   chunk=[];i+=1
   while i<len(lines) and not lines[i].startswith('```'):chunk.append(lines[i]);i+=1
   code('\n'.join(chunk));i+=1;continue
  if line.startswith('|'):
   chunk=[]
   while i<len(lines) and lines[i].startswith('|'):chunk.append(lines[i]);i+=1
   functional_table(chunk);continue
  if line.startswith('## '):heading(line[3:]);i+=1;continue
  if line.startswith('### '):
   if line[4:] in ('Base URL, sessão e leitura dos contratos','Filtros enviados por QueryMap','Corpos e retornos dinâmicos','Autenticação SSO, Firebase e funcionalidades sem REST próprio'):heading(line[4:])
   else:add(inline(line[4:]),'S')
   i+=1;continue
  if line.strip():
   value=line[2:] if line.startswith('- ') else line
   add(inline(value).replace('dicionário de DTOs','dicionário de DTOs (Apêndice B)'))
  i+=1
 source('docs/DATA_ACCESS_BY_FEATURE.md; docs/technical-guide/api-contracts.json. Inventário funcional conferido em 29/10/2026; assinaturas abertas exigem consultar os mapeadores indicados.')

append_data_access_guide()

heading('Autenticação CAIXA: contratos delegados ao AppAuth')
add('Authorization Code com PKCE: o app cria state, nonce e verifier novos por tentativa e abre o browser. O callback é ligado à tentativa original antes da troca. Não há fluxo de senha em cURL nem client secret embarcado. Os comandos abaixo ilustram o protocolo e não substituem a validação de state/nonce/PKCE no app.')
code('GET ${AUTHORIZATION_ENDPOINT}?response_type=code&client_id=${CLIENT_ID}&redirect_uri=${REDIRECT_URI}&state=${STATE}&nonce=${NONCE}&code_challenge=${PKCE_CHALLENGE}&code_challenge_method=S256')
add('O scope é definido pela política capturada. Quando aplicável, openid profile offline_access é usado; deviceid, nivel, app e origem também podem ser enviados conforme a política do dispositivo. Não fabrique identificadores de segurança.')
code('curl --request POST "${TOKEN_ENDPOINT}" \\\n  --header "Content-Type: application/x-www-form-urlencoded" \\\n  --data-urlencode "grant_type=authorization_code" \\\n  --data-urlencode "client_id=${CLIENT_ID}" \\\n  --data-urlencode "redirect_uri=${REDIRECT_URI}" \\\n  --data-urlencode "code=${ONE_TIME_AUTHORIZATION_CODE}" \\\n  --data-urlencode "code_verifier=${PKCE_VERIFIER}"')
code('{"access_token":"TOKEN_FICTICIO","refresh_token":"REFRESH_FICTICIO","id_token":"JWT_FICTICIO","token_type":"Bearer","expires_in":300}')
add('A resposta acima é ilustrativa do conjunto consumido, sem promessa de presença de todos os campos. Renovação envia grant_type=refresh_token, client_id e refresh_token; o contexto de nonce é usado na validação do fluxo AppAuth e não deve ser presumido como um campo adicional do formulário HTTP. Logout OIDC é criado com EndSessionRequest; não confundir com POST usuarios/sair do backend. Tokens reais e códigos de uso único não devem ser copiados para documentos ou histórico de shell.')
source('core/auth/.../appauth/{AppAuthBrowserAuthorization,AppAuthRefresh,AppAuthEndSession}.kt; authorization/AuthorizationRequestPolicy.kt.')
heading('Apêndice A  Contratos HTTP')
add(f'O inventário contém {len(API["operations"])} operações únicas por método/caminho, incluindo todos os {API["baselineCount"]} itens do baseline legado e contratos adicionais encontrados. Interfaces duplicadas de compatibilidade foram consolidadas, priorizando contratos de feature.')
add('Para cada operação: método, rota, declaração Kotlin, parâmetros, cURL e exemplos de request/response. Exemplos são estruturais e sintéticos, sem garantia de validade de negócio. Requests abertos em JsonElement/QueryMap exigem consultar o mapper/fonte; essa ausência de esquema fechado é indicada, em vez de inventar campos obrigatórios.')
add('Os cURLs são templates do contrato lógico. CloudBackendRouting seleciona hosts de publico/logado e pode exigir ocp-apim-subscription-key; o SILCE transforma caminhos, query e body por compatibilidade. Portanto, estes templates não equivalem automaticamente ao tráfego final no wire. O histórico de apostas pode usar host/versão dinâmicos. Consulte os interceptors e providers antes de reproduzir.')
add('Use BASE_URL correspondente ao provider do contrato: backend Loterias, Mercado Pago ou Recarga Pay. Não reutilize bearer de Loterias em outro host. O catálogo não inclui endpoints internos privados dos SDKs Firebase, AppAuth, Topaz ou FilaBRA; eles não estão especificados por estas interfaces.')
add('Operações de escrita, geração de cobrança, resgate, cancelamento e mesmo alguns GETs têm efeitos. Os comandos são documentação e não foram executados. Leia as regras de negócio e use ambiente de testes autorizado antes de qualquer reprodução.')
add('Convenção: LOTERIAS_AUTHORIZATION_HEADER contém o header Authorization completo da sessão capturada, ou fica vazio para uma operação pública sem sessão. BACKEND_SUBSCRIPTION_HEADER contém o header de assinatura da rota cloud quando exigido, ou fica vazio no transporte que não o utiliza. Não se deduz autenticação obrigatória apenas pelo nome do caminho. REQUEST_FILE e BASE_URL são variáveis definidas pelo operador. REQUEST_FILE contém JSON de teste validado no contrato; não coloque credenciais em histórico do shell. Respostas HTTP de erro não têm status/schema universal declarado no Retrofit: consultar BackendHttpFailure e política da feature, sem assumir que toda falha é repetível.')
rows=[['#','Método e caminho','Contrato']]
for i,o in enumerate(API['operations'],1):rows.append([i,o['method']+' '+o['path'],Path(o['source']).name])
table(rows,[24,320,146])
for i,o in enumerate(API['operations'],1):
 heading(f'A{i:03d}  {o["function"]}')
 add(escape(o['method']+' /'+o['path']),'S');source(o['source'])
 if len(o.get('origins',[]))>1:add('Também declarado em: '+escape('; '.join(o['origins'])),'TinyC')
 params=o['parameters'] or '(sem parâmetros)';code('suspend fun '+o['function']+'(\n'+params+'\n): '+o['return'])
 info=[]
 if o['pathParams']:info.append('Path: '+', '.join(o['pathParams']))
 if o['queryParams']:info.append('Query: '+', '.join(o['queryParams']))
 if o['headers']:info.append('Headers explícitos: '+', '.join(o['headers']))
 if o['queryMap']:info.append('QueryMap aberto: chaves dependem do mapper da feature.')
 add(escape(' | '.join(info) or 'Sem parâmetros de URL declarados.'))
 route=re.sub(r'\{([^}]+)\}',r'${\1}',o['path']);url='${BASE_URL%/}/'+route
 if o['queryParams']:url+='?'+'&'.join(k+'=VALOR_TESTE' for k in o['queryParams'])
 curl='curl --request '+o['method']+' \\\n  "'+url+'" \\\n  --header "Accept: application/json"'
 if 'RecargaPay' in o['source']:
  curl+=' \\\n  --header "Authorization: ${RECARGAPAY_AUTHORIZATION}" \\\n  --header "x-api-version: 20220405"'
 elif 'MercadoPago' not in o['source']:
  curl+=' \\\n  --header "${LOTERIAS_AUTHORIZATION_HEADER}" \\\n  --header "${BACKEND_SUBSCRIPTION_HEADER}"'

 if o['bodyType']:curl+=' \\\n  --header "Content-Type: application/json" \\\n  --data-binary "@${REQUEST_FILE}"'
 code(curl)
 if o['bodyType']:
  add('Request - estrutura ilustrativa ('+escape(o['bodyType'])+')','S')
  raw=json.dumps(o['requestExample'],ensure_ascii=False,indent=2)
 
  code(raw)
 else:add('Request: sem body declarado. Parâmetros e headers seguem a assinatura acima.')
 variants=[]
 for variant in o.get('variants',[]):
  match=re.search(r'@Body\s+\w+\s*:\s*([\w<>?, ]+)',variant['parameters'])
  bt=match[1].strip().rstrip(',') if match else None
  if bt and bt!=o['bodyType'] and bt not in variants:
   variants.append(bt);add('Variação de request: '+escape(variant['function'])+' ('+escape(bt)+')','S')
   from inventory_api import sample as variant_sample
   code(json.dumps(variant_sample(bt),ensure_ascii=False,indent=2))
 add('Response - estrutura ilustrativa','S');raw=json.dumps(o['responseExample'],ensure_ascii=False,indent=2)

 code(raw)
 if 'JsonElement' in params+o['return'] or o['queryMap']:add('Contrato parcialmente aberto: o marcador _contrato_aberto é uma anotação documental e não deve ser enviado ao servidor. Campos obrigatórios, alternativas e validações devem ser obtidos no mapper/repository e no contrato oficial do serviço.','TinyC')
 if o['return']=='Unit':add('Unit significa que o cliente não decodifica um corpo. Não implica, por si só, HTTP 204.','TinyC')
 if any(t in o['return'] for t in ['ResponseBody','ByteArray']):add('Resposta binária: não interpretar como JSON. Validar Content-Type e conteúdo esperado, por exemplo PDF, conforme o consumidor.','TinyC')
heading('Apêndice B  Dicionário de DTOs')
add('Campos e tipos extraídos das declarações Kotlin de transporte. @SerializedName define o nome de wire. Nullable e valores default do cliente não provam opcionalidade no servidor. Campos de JSON aberto, enumerações e restrições comerciais exigem o contrato oficial.')
for name,m in sorted(API['models'].items()):
 if not m['fields']:continue
 add(escape(name),'S');source(m['source']);table([['Campo JSON','Tipo Kotlin']]+[[x['name'],x['type']] for x in m['fields']],[220,270])
heading('Referências e rastreabilidade')
for ref in ['settings.gradle; gradle/libs.versions.toml; app/build.gradle','ARCHITECTURE.md; docs/FEATURE_MVVM_CONTRACT.md; docs/QUALITY_GATE.md','docs/LEGACY_BACKEND_BASELINE.tsv; interfaces Retrofit no core/network e nas features','docs/MARKETPLACE_FILTER_PAIRED_PARITY_20261015.md; build/verification/experimente-20261021','core/design-system/README.md; sample/design-system/README.md','scripts/sign-remote-config-pins.py; scripts/rotate-remote-config-pins.py','docs/superpowers/specs/2026-10-22-encrypted-datastore-recovery-design.md','docs/superpowers/plans/2026-10-22-encrypted-datastore-implementation.md']:
 add(escape(ref))
add('Esta edição foi produzida a partir de fontes locais. Contratos dos provedores externos e comportamento operacional devem ser confirmados com seus responsáveis. Exemplos não incluem dados reais de usuário, pins instalados, certificados privados, tokens ou chaves privadas.')
def page(c,doc):
 c.saveState();w,h=doc.pagesize
 if doc.page==1:
  c.setFillColor(BLUE);c.rect(0,h-205,w,205,fill=1,stroke=0);logo=R/'core/design-system/src/main/res/drawable-nodpi/caixa_loterias_logo.png';c.drawImage(str(logo),44,h-165,width=125,height=128,mask='auto',preserveAspectRatio=True);c.setFillColor(ORANGE);c.rect(0,h-210,w,5,fill=1,stroke=0)
 else:
  c.setFillColor(BLUE);c.rect(0,h-32,w,32,fill=1,stroke=0);c.setFillColor(white);c.setFont('CaixaBold',9);c.drawString(43,h-21,'LOTERIAS CAIXA  |  DOCUMENTAÇÃO TÉCNICA');c.setFillColor(ORANGE);c.rect(0,h-35,w,3,fill=1,stroke=0)
 c.setFillColor(INK);c.setFont('Caixa',8);c.drawString(43,24,EDITION_DATE+'  •  Exemplos sintéticos  •  Uso técnico');c.drawRightString(w-43,24,str(doc.page));c.restoreState()
class GuideDoc(SimpleDocTemplate):
 def beforeDocument(self):
  self.outline_count=0
 def afterFlowable(self,flowable):
  if isinstance(flowable,Paragraph) and flowable.style.name=='H':
   key='section-'+str(self.page)+'-'+str(getattr(self,'outline_count',0));self.outline_count=getattr(self,'outline_count',0)+1
   self.canv.bookmarkPage(key);self.canv.addOutlineEntry(flowable.getPlainText(),key,0,False)
   title=flowable.getPlainText()
   if re.match(r'^(?:0[1-9]|10)\s',title) or title.startswith(('Apêndice ', 'Referências e rastreabilidade')):
    self.notify('TOCEntry',(0,title,self.page,key))
OUT.parent.mkdir(parents=True,exist_ok=True)
doc=GuideDoc(str(OUT),pagesize=(595.28,841.89),rightMargin=43,leftMargin=43,topMargin=57,bottomMargin=45,title='Loterias CAIXA - Guia técnico e catálogo de APIs',author='Documentação técnica do projeto SIMLO')
doc.multiBuild(story,onFirstPage=page,onLaterPages=page);print(OUT)
