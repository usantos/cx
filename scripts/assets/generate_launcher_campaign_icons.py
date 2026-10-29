#!/usr/bin/env python3
"""Reproduce campaign resources from the extracted authoritative October artwork.

Requires Pillow (tested with 12.3.0). Run from any directory; no Gradle/ADB needed.
October image/background bytes are copied; adaptive XML only renames references.
Other campaigns recolor saturated magenta, preserving every other RGBA pixel.
"""
from __future__ import annotations

import argparse
import colorsys
import hashlib
import json
import re
from collections import Counter
from pathlib import Path
from xml.etree import ElementTree as ET

from PIL import Image, ImageDraw, ImageFont

ROOT = Path(__file__).resolve().parents[2]
SOURCE = ROOT / "build/verification/experimente-delta-20261025/legacy-res"
RES = ROOT / "app/src/main/res"
OUT = ROOT / "output/launcher-icons"
DENSITIES = ("mdpi", "hdpi", "xhdpi", "xxhdpi", "xxxhdpi")
CAMPAIGNS = {
    "outubro_rosa": (None, 1.0, "#EE3E7F"),
    "setembro_amarelo": (48.0, 1.0, "#EECB3E"),
    "novembro_azul": (212.0, 1.0, "#3E90EE"),
    "agosto_lilas": (275.0, 0.65, "#BE7CEE"),
}
NS = "{http://schemas.android.com/apk/res/android}"


def sha(path: Path) -> str:
    return hashlib.sha256(path.read_bytes()).hexdigest()


def campaign_pixel(pixel):
    r, g, b, _ = pixel
    return r - g >= 20 and b - g >= 15


def recolor(pixel, hue, saturation_scale):
    if not campaign_pixel(pixel):
        return pixel
    r, g, b, a = pixel
    _, saturation, value = colorsys.rgb_to_hsv(r / 255, g / 255, b / 255)
    rgb = colorsys.hsv_to_rgb(hue / 360, saturation * saturation_scale, value)
    return (*[round(channel * 255) for channel in rgb], a)


def write(path: Path, content: bytes):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_bytes(content)


def render_background(path: Path, size: int):
    """Rasterize this legacy vector's rectangles and straight grid lines."""
    im = Image.new("RGBA", (size, size))
    draw = ImageDraw.Draw(im)
    scale = size / 108
    for node in ET.parse(path).getroot():
        fill = node.get(NS + "fillColor", "#00000000")
        data = node.get(NS + "pathData", "")
        if "h108" in data:
            draw.rectangle((0, 0, size, size), fill=fill)
        elif node.get(NS + "strokeColor"):
            coords = [float(v) for v in re.findall(r"-?\d+(?:\.\d+)?", data)]
            layer = Image.new("RGBA", im.size)
            color = node.get(NS + "strokeColor")
            # O hexadecimal Android é AARRGGBB; o do Pillow é RRGGBBAA.
            color = "#" + color[3:] + color[1:3]
            ImageDraw.Draw(layer).line(tuple(v * scale for v in coords), fill=color,
                                       width=max(1, round(float(node.get(NS + "strokeWidth")) * scale)))
            im = Image.alpha_composite(im, layer)
            draw = ImageDraw.Draw(im)
    return im


def adaptive_preview(foreground: Path, background, size=288):
    # Camadas adaptativas têm 108dp; os 72dp centrais são a área visível do inicializador.
    layer_size = 432
    bg = (Image.new("RGBA", (layer_size, layer_size), background)
          if isinstance(background, str) else render_background(background, layer_size))
    fg = Image.open(foreground).convert("RGBA").resize(bg.size, Image.Resampling.LANCZOS)
    combined = Image.alpha_composite(bg, fg).crop((72, 72, 360, 360))
    mask = Image.new("L", combined.size)
    ImageDraw.Draw(mask).rounded_rectangle((0, 0, 287, 287), radius=66, fill=255)
    combined.putalpha(mask)
    return combined.resize((size, size), Image.Resampling.LANCZOS)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--check", action="store_true", help="Verify existing generated files and provenance without writing")
    args = parser.parse_args()
    if args.check:
        provenance = json.loads((OUT / "provenance.json").read_text())
        for entry in provenance["sources"] + provenance["outputs"]:
            assert sha(ROOT / entry["path"]) == entry["sha256"], entry["path"]
        print(f'PASS: {len(provenance["sources"])} source hashes and {len(provenance["outputs"])} output hashes')
        return
    OUT.mkdir(parents=True, exist_ok=True)
    provenance = {
        "source": str(SOURCE.relative_to(ROOT)),
        "method": "Pillow RGBA; magenta iff R-G>=20 and B-G>=15; replace HSV hue, scale saturation, preserve V and alpha; Python round to nearest integer; lossless WebP exact=True",
        "october_fidelity": "All 15 WebP files and background vector copied byte-for-byte; two adaptive XML files only rename resource references",
        "adaptive_background_note": "October retains legacy Android green sample grid (#3DDC84). Derived campaigns retain the grid geometry/white strokes and replace its green fill with campaign color.",
        "preview_note": "Illustrative rounded-square adaptive mask, central 72dp of 108dp layers; device launcher masks/cache may differ. Default from existing foreground and build.gradle #0077C8.",
        "palettes": {}, "sources": [], "pixel_checks": [], "outputs": [],
    }
    sources = sorted(SOURCE.glob("mipmap-*/ic_launcher_rosa*.webp"))
    sources += sorted(SOURCE.glob("mipmap-anydpi-v26/ic_launcher_rosa*.xml"))
    sources += [SOURCE / "drawable/ic_launcher_rosa_background.xml"]
    provenance["sources"] = [{"path": str(p.relative_to(ROOT)), "sha256": sha(p)} for p in sources]
    default_files = [RES / "mipmap-xxxhdpi/ic_launcher_foreground.webp", RES / "mipmap-anydpi/ic_launcher.xml", ROOT / "app/build.gradle"]
    default_hashes = {str(p.relative_to(ROOT)): sha(p) for p in default_files}
    output_paths = []
    for campaign, (hue, saturation_scale, background) in CAMPAIGNS.items():
        prefix = "ic_launcher_" + campaign
        provenance["palettes"][campaign] = {"hue_degrees": hue, "saturation_multiplier": saturation_scale, "adaptive_background_fill": background}
        for source in sources:
            folder = "mipmap-anydpi" if source.parent.name == "mipmap-anydpi-v26" else source.parent.name
            packaged = source.suffix != ".webp" or source.name.endswith("_foreground.webp")
            destination_root = RES if packaged else OUT / "legacy"
            dest = destination_root / folder / source.name.replace("ic_launcher_rosa", prefix)
            if source.suffix == ".webp":
                original = Image.open(source).convert("RGBA")
                pixels = list(original.get_flattened_data())
                expected = pixels if hue is None else [recolor(p, hue, saturation_scale) for p in pixels]
                if hue is None:
                    write(dest, source.read_bytes())
                else:
                    result = Image.new("RGBA", original.size)
                    result.putdata(expected)
                    dest.parent.mkdir(parents=True, exist_ok=True)
                    result.save(dest, "WEBP", lossless=True, exact=True, method=6)
                actual = list(Image.open(dest).convert("RGBA").get_flattened_data())
                assert actual == expected, dest
                assert all(a[3] == b[3] for a, b in zip(pixels, actual)), dest
                preserved = sum(not campaign_pixel(p) for p in pixels)
                assert all(a == b for a, b in zip(pixels, actual) if not campaign_pixel(a)), dest
                provenance["pixel_checks"].append({"path": str(dest.relative_to(ROOT)), "size": list(original.size), "alpha_unchanged": True, "noncampaign_rgba_unchanged": preserved, "campaign_pixels": len(pixels)-preserved, "transformed_values_most_common": [{"source": list(p), "output": list(p if hue is None else recolor(p, hue, saturation_scale)), "count": count} for p, count in Counter(pixels).most_common(8)]})
            else:
                content = source.read_bytes()
                if source.parent.name == "mipmap-anydpi-v26":
                    content = content.replace(b"ic_launcher_rosa", prefix.encode())
                    content = content.replace(
                        b"</adaptive-icon>",
                        b'<monochrome android:drawable="@mipmap/' + prefix.encode()
                        + b'_foreground" />\n</adaptive-icon>',
                    )
                elif hue is not None:
                    content = content.replace(b"#3DDC84", background.encode())
                write(dest, content)
                if source.parent.name == "drawable" and hue is None:
                    assert dest.read_bytes() == source.read_bytes()
                ET.parse(dest)
            output_paths.append(dest)
        preview = adaptive_preview(RES / "mipmap-xxxhdpi" / (prefix + "_foreground.webp"), RES / "drawable" / (prefix + "_background.xml"))
        preview_path = OUT / (campaign + "-adaptive.png")
        preview.save(preview_path)
        output_paths.append(preview_path)
    default_preview = adaptive_preview(default_files[0], "#0077C8")
    default_preview.save(OUT / "default-adaptive.png")
    output_paths.append(OUT / "default-adaptive.png")
    labels = [("default", "Padrão"), ("outubro_rosa", "Outubro Rosa"), ("setembro_amarelo", "Setembro Amarelo"), ("novembro_azul", "Novembro Azul"), ("agosto_lilas", "Agosto Lilás")]
    comparison = Image.new("RGB", (1700, 790), "#f2f4f7")
    draw = ImageDraw.Draw(comparison)
    try:
        font = ImageFont.truetype("/System/Library/Fonts/Supplemental/Arial.ttf", 24)
        small = ImageFont.truetype("/System/Library/Fonts/Supplemental/Arial.ttf", 19)
    except OSError:
        font = ImageFont.load_default(size=24)
        small = ImageFont.load_default(size=19)
    draw.text((30, 18), "Ícones do launcher — cinco opções", font=font, fill="#263547")
    draw.text((30, 58), "Prévia adaptativa (recorte central de 72dp, máscara ilustrativa)", font=small, fill="#526273")
    draw.text((30, 438), "Recursos legados de campanha (48dp; transparência preservada)", font=small, fill="#526273")
    for index, (key, label) in enumerate(labels):
        x = 26 + index * 338
        im = Image.open(OUT / (key + "-adaptive.png"))
        comparison.paste(im, (x, 100), im)
        draw.text((x, 398), label, font=font, fill="#263547")
        if key == "default":
            draw.text((x, 590), "Ícone atual preservado", font=small, fill="#526273")
        else:
            legacy = Image.open(OUT / "legacy/mipmap-xxxhdpi" / ("ic_launcher_" + key + ".webp")).convert("RGBA")
            legacy = legacy.resize((240, 240), Image.Resampling.LANCZOS)
            comparison.paste(legacy, (x + 24, 486), legacy)
    draw.text((30, 750), "Outubro: arte original e fundo adaptativo originais. Demais campanhas: mesma forma, marca e alpha; paleta derivada.", font=small, fill="#526273")
    comparison.save(OUT / "five-launcher-icons-comparison.png")
    output_paths.append(OUT / "five-launcher-icons-comparison.png")
    assert all(sha(ROOT / p) == digest for p, digest in default_hashes.items())
    provenance["default_unchanged_hashes"] = default_hashes
    provenance["outputs"] = [{"path": str(p.relative_to(ROOT)), "sha256": sha(p)} for p in sorted(output_paths)]
    (OUT / "provenance.json").write_text(json.dumps(provenance, indent=2, ensure_ascii=False) + "\n")
    (OUT / "generated-files.txt").write_text("\n".join(str(p.relative_to(ROOT)) for p in sorted(output_paths)) + "\n")
    print(f"PASS: generated {len(output_paths)} files; 60 image alpha/noncampaign pixel checks; original October images/background exact; default unchanged")


if __name__ == "__main__":
    main()
