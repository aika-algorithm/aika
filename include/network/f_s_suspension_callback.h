#ifndef NETWORK_F_S_SUSPENSION_CALLBACK_H
#define NETWORK_F_S_SUSPENSION_CALLBACK_H

#include "network/suspension_callback.h"
#include "network/model.h"
#include <filesystem>
#include <fstream>
#include <map>
#include <string>
#include <vector>

class FSSuspensionCallback : public SuspensionCallback {
public:
    FSSuspensionCallback(const std::filesystem::path& path, const std::string& modelLabel);

    void prepareNewModel() override;
    void open() override;
    void close() override;
    long createId() override;
    long getCurrentId() const override;
    void store(long id, const std::vector<char>& data) override;
    std::vector<char> retrieve(long id) override;
    void remove(long id) override;
    void loadIndex(Model* m) override;
    void saveIndex(Model* m) override;

private:
    std::filesystem::path getFile(const std::string& prefix) const;

    std::filesystem::path path;
    std::string modelLabel;
    long currentId;
    std::fstream modelStore;
    std::map<int, long> tokenIds;
    std::map<long, std::pair<long, int>> index;
};

#endif // NETWORK_F_S_SUSPENSION_CALLBACK_H 