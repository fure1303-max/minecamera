# MineCamera

中文简介：`MineCamera` 是一个面向 `Minecraft Java 1.21.11` 的 `Fabric` 相机模组。  
English: `MineCamera` is a `Fabric` camera mod for `Minecraft Java 1.21.11`.

## Version Baseline

- Minecraft: `1.21.11`
- Fabric Loader: `0.19.3`
- Fabric API: `0.141.4+1.21.11`
- Java: `21`

This repository is tested against `Fabric Loader 0.19.3`. It is not advertised as a generic "latest Fabric" build.

## Features in v1.0

- Handheld camera HUD with live viewfinder
- Tripod placement and mounted camera workflow
- Photo capture to local `PNG` files
- Film workflow with blank and exposed film
- In-HUD photo review mode
- Photo display frames with auto-grouped large-image slicing
- Resolution presets and manual focal-length adjustment
- Local media index and client-side image playback
- Baseline compatibility work for Sodium/Iris setups

## Not in v1.0

- Real video recording/export
- Final depth-of-field and aperture-based blur pipeline
- Final autofocus implementation
- Final tripod top-mounted 3D camera model

## Installation

1. Install `Minecraft Java 1.21.11`.
2. Install `Fabric Loader 0.19.3`.
3. Install `Fabric API`.
4. Put the built `minecamera` jar into your `mods` folder.
5. Launch the game with `Java 21`.

## Building

```bash
./gradlew build
```

Built jars are generated under `build/libs/`.

## Usage

- 中文详细说明: [docs/USAGE.zh-CN.md](docs/USAGE.zh-CN.md)
- Quick summary:
  - Right click with the camera to open the HUD
  - Left click to capture in live mode
  - Right click to close the camera HUD
  - `Tab` switches resolution presets
  - Mouse wheel changes focal length
  - `R` switches between live view and review
  - `A / D` flips through exposed film in review mode

## License

This project is licensed under the `MIT` license. See [LICENSE](LICENSE).
